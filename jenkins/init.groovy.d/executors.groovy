import jenkins.*
import hudson.*
import hudson.model.*
import jenkins.model.*
import hudson.security.*
import org.apache.commons.lang3.RandomStringUtils
import com.cloudbees.plugins.credentials.*
import com.cloudbees.plugins.credentials.impl.*
import com.cloudbees.plugins.credentials.domains.Domain

def instance = Jenkins.getInstance()

//no executtors on master, only on slave agents
instance.setNumExecutors(0)
instance.setSlaveAgentPort([55000])

def all = CredentialsProvider.lookupCredentials(
    Credentials.class,
    jenkins.model.Jenkins.instance
)
all.each {
    if (it.getId() == "devops-system") {
        //write out the password to a local shared volume with agent
        def pword = it.getPassword().toString();
        def home = System.getenv().JENKINS_HOME
        def pwordfile = new File("$home/secrets/jenkins-agent.txt")
        pwordfile.write(pword, 'UTF-8')
    }
}

instance.save()

