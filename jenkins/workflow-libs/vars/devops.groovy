/**
Reads the jenkins.properties file from project root. If not available or
missing properties, it will use local environment properties to 
supplement.
*/
def getProjectProperties(scope) {
    scope.with {
        def props = readProperties file: './jenkins.properties', defaults: [
            'app.id': env.JOB_NAME.replaceAll(/[\\\/\s]/, ":"),
            'app.name': env.JOB_NAME,
            'app.version': env.GIT_COMMIT,
            'app.repo': env.GIT_URL
        ]

        props['project.branch'] = env.BRANCH_NAME ?: 'master'
        props['project.releaseable'] = props['project.branch'] == 'master' || props['project.branch'].startsWith('release')
        props['project.artifacts.target'] = "${props['app.name']}/${props['app.version']}"

        return props
    }
}

/**
Helper function when pushing project to SonarQube, handles authentication, integration,
and forcing certain properties to SonarQube to override what user may place in their
local sonar-project.properties file.
*/
def pushSonarQube(scope) {
    scope.with {
        //https://docs.sonarqube.org/display/SONAR/Analysis+Parameters
        def props = getProjectProperties(scope)
        withSonarQubeEnv('sonarqube') {
            sh """${tool name: 'sonarscanner'}/bin/sonar-scanner \
            -Dsonar.host.url=${SONAR_HOST_URL} \
            -Dsonar.login=${SONAR_AUTH_TOKEN} \
            -Dsonar.projectName=${props['app.name']} \
            -Dsonar.projectVersion=${props['app.version']} \
            -Dsonar.projectKey=${props['app.id']} \
            -Dsonar.links.scm=${props['app.repo']} \
            -Dsonar.links.ci=${env.JENKINS_URL}job/${env.JOB_NAME}"""
        }
        waitSonarQube(scope)
    }
}

def waitSonarQube(scope) {
    scope.with {
        timeout(time: 10, unit: 'MINUTES') {
            def result = waitForQualityGate()
            if (result.status != "OK") {
                error "Pipeline aborted due to quality gate failure: ${result.status}" 
            }
        }
    }
}

/**
Helper function when pushing project to Artifactory, handles authentication, integration,
and forcing certain artifacts to Artifactory in addition to the artifacts the user 
requested to archive.
*/
def pushArtifactory(scope, artifacts) {
    scope.with {
        //only push if passed quality gate
        waitSonarQube(scope)

        def props = getProjectProperties(scope)
        def server = Artifactory.server 'artifactory'
        def files = artifacts.collect{"""{"pattern":"${it}","target":"${props['project.artifacts.target']}/"}"""}
        def spec = """{"files":[
            {"pattern": "_change.log","target":"${props['project.artifacts.target']}/"},
            {"pattern": "_build.log","target":"${props['project.artifacts.target']}/"},
            {"pattern": "**/*.feature","target":"${props['project.artifacts.target']}/_features/"},
            {"pattern": "*.properties","target":"${props['project.artifacts.target']}/_properties/"},
            ${files.join(',')}
        ]}"""

        if (props['project.releaseable']) {
            //ensure that a repo already exists for app id before we upload artifacts, if its already there will just silently continue
            //note that official API to create repo only works on Artifactory PRO, not OSS, so we use the same API that GUI calls instead
            def url = server.getUrl()
            def user = "${server.getUsername()}:${server.getPassword()}"
            def repo = """{
                "type":"localRepoConfig",
                "typeSpecific":
                {
                    "localChecksumPolicy":"CLIENT",
                    "repoType":"Generic",
                    "icon":"generic",
                    "text":"Generic",
                    "listRemoteFolderItems":true,
                    "url":""
                },
                "advanced":
                {
                    "cache":
                        {
                            "keepUnusedArtifactsHours":"",
                            "retrievalCachePeriodSecs":600,
                            "assumedOfflineLimitSecs":300,
                            "missedRetrievalCachePeriodSecs":1800
                        },
                    "network":
                        {
                            "socketTimeout":15000,
                            "syncProperties":false,
                            "lenientHostAuth":false,
                            "cookieManagement":false
                        },
                    "blackedOut":false,
                    "allowContentBrowsing":false
                },
                "basic":
                {
                    "includesPattern":"**/*",
                    "includesPatternArray":["**/*"],
                    "excludesPatternArray":[],
                    "layout":"simple-default"
                },
                "general":
                {
                    "repoKey":"${props['app.id']}"
                }
            }"""

            sh "#!/bin/sh -e\n curl -X POST -H 'Content-Type: application/json' -d '${repo}' --user '${user}' '${url}/ui/admin/repositories'"
            sh "git log --pretty=medium > _change.log"
            writeFile file: '_build.log', text: currentBuild.rawBuild.log, encoding: 'UTF-8'
            
            def build = Artifactory.newBuildInfo() 
            build.env.capture = true
            server.upload(spec)
            server.publishBuildInfo(build)

            //TODO: create local artifact with props data using JSON or properties file?
        }
        else {
            echo "Branch ${props['project.branch']} is not releaseable for artifactory"
        }
    }
}

/**
Helper function that notifies all other integrated services about the status
of the current build as a final post action.
*/
def pushNotifications(scope) {
    scope.with {
        echo "TODO: notify Gitlab, RocketChat, GoCD..."
    }
}

/*
This is all temp stuff for now, WIP
*/
def deployGCPAppEngine(scope, imageFile, imageName, projectId) {
    scope.with {
        def props = getProjectProperties(scope)
        /*def server = Artifactory.server 'artifactory'
        def files = """{"files":[
                {"pattern": "${imageFile}","target":"${props['project.artifacts.target']}/"}
            ]}"""
        server.download(files)*/
        
        withCredentials([usernameColonPassword(credentialsId: 'devops-system', variable: 'USERPASS')]) {
            sh "curl -u $USERPASS http://artifactory:8081/artifactory/${props['project.artifacts.target']}/${imageFile} > ${imageFile}"
        }
        
        docker.image('google/cloud-sdk').inside('-v /var/run/docker.sock:/var/run/docker.sock') {
            withCredentials([
                string(credentialsId: 'gcp-account', variable: 'GCP_ACCOUNT'), 
                file(credentialsId: 'gcp-key-file', variable: 'GCP_KEY_FILE')]) {
                
                sh "gcloud auth activate-service-account $GCP_ACCOUNT --key-file=$GCP_KEY_FILE"
                sh "gcloud auth configure-docker"
                sh "docker image load -i ./${imageFile}"
                sh "docker tag ${imageName} gcr.io/${projectId}/${imageName}"
                sh "docker image push gcr.io/${projectId}/${imageName}"
                sh "gcloud app create --project $projectId --region=us-central || echo 'app already exists...continuing'"
                sh "echo runtime: custom > app.yaml"
                sh "echo env: flex >> app.yaml"
                sh "gcloud app deploy --project $projectId --promote --image-url gcr.io/${projectId}/${imageName}"
            }
        }
    }
}