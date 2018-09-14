import jenkins.*
import hudson.*
import jenkins.model.*
import hudson.util.Secret
import jenkins.security.*
import com.cloudbees.plugins.credentials.*
import com.cloudbees.plugins.credentials.impl.*
import com.dabsquared.gitlabjenkins.connection.*
import com.cloudbees.plugins.credentials.domains.Domain

def getOrCreateCredentials(defaultCredentials) {
    def all = CredentialsProvider.lookupCredentials(
        Credentials.class,
        jenkins.model.Jenkins.instance
    )
    def credentials = all.findResult { it.getId() == defaultCredentials.getId() ? it : null }
    if (credentials == null) {
        println "creating new credentials for ${defaultCredentials.getId()}"
        def store = SystemCredentialsProvider.getInstance().getStore();
        credentials = defaultCredentials
        store.addCredentials(Domain.global(), credentials)
    }
    else {
        println "credentials for ${defaultCredentials.getId()} already exist"
    }
    return credentials
}

def credentials = getOrCreateCredentials(new GitLabApiTokenImpl(
    CredentialsScope.GLOBAL,
    "gitlab", 
    "Gitlab API token - NO RESTART NEEDED ON CHANGE", 
    new Secret("")  //just a default value, user should create on gitlab update post initial run
))

def plugin = Jenkins.instance.getDescriptor("com.dabsquared.gitlabjenkins.connection.GitLabConnectionConfig")
def connections = new ArrayList<>();
def gitlabConfig = new GitLabConnection(
    "gitlab",
    "http://gitlab:80/gitlab/",
    credentials.getId(),
    true,
    10,
    10
)
connections.add(gitlabConfig)
plugin.setConnections(connections)
plugin.save()

//TODO: setup a timer, check for access to all project repos, make sure we have a matching jenkins project