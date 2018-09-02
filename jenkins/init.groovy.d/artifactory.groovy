import jenkins.*
import hudson.*
import jenkins.model.*
import org.jfrog.*
import org.jfrog.hudson.*
import com.cloudbees.plugins.credentials.*
import com.cloudbees.plugins.credentials.impl.*
import com.cloudbees.plugins.credentials.domains.Domain

def getSystemCredentials() {
    def id = "devops-system"
    def all = CredentialsProvider.lookupCredentials(
        Credentials.class,
        jenkins.model.Jenkins.instance
    )
    return all.findResult { it.getId() == id ? it : null }
}

def inst = Jenkins.getInstance()
def credentials = getSystemCredentials()
def desc = inst.getDescriptor("org.jfrog.hudson.ArtifactoryBuilder")
def config = new CredentialsConfig(
  credentials.getUsername(), 
  credentials.getPassword().toString(), 
  credentials.getId(), 
  true
)
def server = new ArtifactoryServer(
  "artifactory",
  "http://artifactory:8081/artifactory",
  config,
  config,
  300,
  false,
  3 
)
desc.setArtifactoryServers([server])
desc.setUseCredentialsPlugin(true)
desc.save()