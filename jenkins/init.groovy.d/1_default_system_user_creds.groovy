import jenkins.*
import hudson.*
import jenkins.model.*
import org.jfrog.*
import org.jfrog.hudson.*
import com.cloudbees.plugins.credentials.*
import com.cloudbees.plugins.credentials.impl.*
import com.cloudbees.plugins.credentials.domains.Domain

def all = CredentialsProvider.lookupCredentials(
    Credentials.class,
    jenkins.model.Jenkins.instance
)
def credentials = all.findResult { it.getId() == "devops-system" ? it : null }

if (credentials == null) {
    println "creating new credentials for devops-system"
    def store = SystemCredentialsProvider.getInstance().getStore();
    store.addCredentials(Domain.global(), new UsernamePasswordCredentialsImpl(
        CredentialsScope.GLOBAL,
        "devops-system", 
        "LDAP system username and password - RESTART JENKINS AND AGENTS ON CHANGE", 
        "devops-system", 
        "P@ssw0rd" //just the default value, user should update post run
    ))
}