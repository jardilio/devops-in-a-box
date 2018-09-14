/*
Purpose of this is to find all repos given credentials have
access to and ensure we have a job matching that repo. By
default we create a multibranch pipeline job.
*/

import hudson.util.PersistedList
import jenkins.model.Jenkins
import jenkins.branch.*
import jenkins.plugins.git.*
import org.jenkinsci.plugins.workflow.multibranch.*
import com.cloudbees.hudson.plugins.folder.*
import com.cloudbees.plugins.credentials.*
import com.cloudbees.plugins.credentials.impl.*
import com.cloudbees.plugins.credentials.domains.Domain
import groovy.json.JsonSlurper
import jenkins.model.JenkinsLocationConfiguration

def getCredentials(String id) {
    def all = CredentialsProvider.lookupCredentials(
        Credentials.class,
        jenkins.model.Jenkins.instance
    )
    return all.findResult { it.getId() == id ? it : null }
}

def getJenkinsAuthenticatedUrl() {
    //def sysCredentials = getCredentials('devops-system')
    def jenkinsUrl = JenkinsLocationConfiguration.get().getUrl() 
    //just incase we are running on local host, we need to use the internal service name to resolve
    jenkinsUrl = jenkinsUrl.replace("//localhost", "//jenkins:8080")
    //use token authentication for webhooks
    //jenkinsUrl = jenkinsUrl.replace("://", "://${sysCredentials.getUsername()}:${}@")
    return jenkinsUrl
}

def getConnection(String url) {
    return new URL(url).openConnection() as HttpURLConnection
}

def send(HttpURLConnection connection) {
    return send(connection, "GET", null)
}

def send(HttpURLConnection connection, String method, String body) {
    connection.setRequestMethod(method)
    connection.setRequestProperty('Accept', 'application/json')
    connection.setRequestProperty('Content-Type', 'application/json')

    if (body != null) {
        connection.setDoOutput(true)

        OutputStreamWriter wr = new OutputStreamWriter(connection.getOutputStream())
        wr.write(body)
        wr.flush()
    }

    def responseCode = null

    try {
        responseCode = connection.responseCode
    }
    catch (e) {
        echo e.toString()
    }

    if (responseCode == 200 || responseCode == 201) {
        return new JsonSlurper().parseText(connection.inputStream.text)
    }
    else {
        error responseCode + ": " + connection.inputStream.text
    }
}

def syncInternalGitlabProjects() {
    syncGitlabProjects('http://gitlab/gitlab', 'gitlab', 'devops-system')
}

def syncExternalGitlabProjects() {
    syncGitlabProjects('https://gitlab.com', 'gitlab-public', 'gitlab-public')
}

def syncExternalGithubProjects() {
    echo "TODO: syncPublicGithubProjects"
}

def syncExternalBitbucketProjects() {
    echo "TODO: syncPublicBitbucketProjects"
}

def syncGitlabProjects(String url, String apiCredentialsId, String checkoutCredentialsId) {
    def credentials = getCredentials(apiCredentialsId)
    if (credentials != null) {
        echo "Pulling projects from ${url}"

        def project = getConnection("${url}/api/v4/projects?membership=true")
        project.setRequestProperty('Private-Token', credentials.getApiToken().getPlainText())

        def result = send(project)
        if ( result != null ) {
            result.forEach { item -> 
                //internal gitlab may report as localhost rather than internal service name or external domain name
                def jobCreated = ensureJob(item.namespace.name, item.name, item.http_url_to_repo.replace("//localhost", "//gitlab"), checkoutCredentialsId)
                if (jobCreated) {
                    echo "Creating webhook from ${url} to job ${item.namespace.name}/${item.name}"

                    def hook = getConnection("${url}/api/v4/projects/${item.id}/hooks")
                    hook.setRequestProperty('Private-Token', credentials.getApiToken().getPlainText())
                    send(hook, "POST", """{
                        "url": "${getJenkinsAuthenticatedUrl()}project/${item.namespace.name}/${item.name}",
                        "push_events": true
                    }""")
                }
            }
        }
    }
    else {
        echo "Need to create credentials for ${apiCredentialsId} to ${url}"
    }
}

def ensureJob(String folderName, String jobName, String gitRepo, String credentialsId) {
    def inst = Jenkins.getInstance()
    def folder = inst.getItem(folderName) ?: inst.createProject(Folder.class, folderName)
    def item = folder.getItem(jobName)

    echo "Checking for job ${folderName}/${jobName} to ${gitRepo}"

    if ( item == null ) {
        echo "Creating job ${folderName}/${jobName} to ${gitRepo}"

        WorkflowMultiBranchProject job = folder.createProject(WorkflowMultiBranchProject.class, jobName)
        GitSCMSource gitSCMSource = new GitSCMSource('', gitRepo, credentialsId, '*', '', false)
        BranchSource branchSource = new BranchSource(gitSCMSource)
        PersistedList sources = job.getSourcesList()
        sources.clear()
        sources.add(branchSource)

        return true
    } 
    else {
        echo "Job ${folderName}/${jobName} already exists"
    }

    return false
}