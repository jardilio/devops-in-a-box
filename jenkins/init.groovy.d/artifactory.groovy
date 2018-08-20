import jenkins.*
import hudson.*
import jenkins.model.*
import org.jfrog.*
import org.jfrog.hudson.*
import org.jfrog.hudson.util.Credentials;

def inst = Jenkins.getInstance()
def env = System.getenv()

def desc = inst.getDescriptor("org.jfrog.hudson.ArtifactoryBuilder")

def deployerCredentials = new CredentialsConfig("devops-system", "${env.DOS_SYSTEM_USER_PASSWORD}", "")
def resolverCredentials = new CredentialsConfig("", "", "")

def sinst = [new ArtifactoryServer(
  "artifactory",
  "http://artifactory:8081/artifactory",
  deployerCredentials,
  resolverCredentials,
  300,
  false,
  3 )
]

desc.setArtifactoryServers(sinst)

desc.save()