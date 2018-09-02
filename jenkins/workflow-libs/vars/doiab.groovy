/**
Reads the jenkins.properties file from project root. If not available or
missing properties, it will use local environment properties to 
supplement.
*/
def getProjectProperties(script) {
    script.with {
        def props = readProperties  file: './jenkins.properties', defaults: [
            'app.id': env.JOB_NAME,
            'app.name': env.JOB_NAME,
            'app.version': env.GIT_COMMIT ?: 'latest',
            'app.repo': env.GIT_URL
        ]
        props['project.branch'] = env.BRANCH_NAME ?: 'master'
        props['project.releaseable'] = props['project.branch'] == 'master' || props['project.branch'].startsWith('release')
        return props
    }
}

/**
Helper function when pushing project to SonarQube, handles authentication, integration,
and forcing certain properties to SonarQube to override what user may place in their
local sonar-project.properties file.
*/
def pushSonarQube(script) {
    script.with {
        //https://docs.sonarqube.org/display/SONAR/Analysis+Parameters
        def props = getProjectProperties(script)
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
        /*
        TODO: need to setup a webhook from sonarqube back to jenkins for below to work
        timeout(time: 10, unit: 'MINUTES') {
            def result = waitForQualityGate()
            if (result.status != "OK") {
                error "Pipeline aborted due to quality gate failure: ${result.status}" 
            }
        }*/
    }
}

/**
Helper function when pushing project to Artifactory, handles authentication, integration,
and forcing certain artifacts to Artifactory in addition to the artifacts the user 
requested to archive.
*/
def pushArtifactory(script, artifacts) {
    script.with {
        def props = getProjectProperties(script)
        def server = Artifactory.server 'artifactory'
        def files = artifacts.collect{"""{"pattern":"${it}","target":"${props['app.id']}/${props['app.version']}/"}"""}
        def spec = """{"files":[
            {"pattern": "**/*.feature","target":"${props['app.id']}/${props['app.version']}/_features_/"},
            {"pattern": "*.properties","target":"${props['app.id']}/${props['app.version']}/_properties_/"},
            ${files.join(',')}
        ]}"""

        if (props['project.releaseable']) {
            server.upload(spec)
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
def pushNotifications(script) {
    script.with {
        echo "TODO: notify Gitlab, RocketChat, GoCD..."
    }
}