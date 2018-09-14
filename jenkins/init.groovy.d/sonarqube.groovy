import jenkins.*
import hudson.*
import jenkins.model.*
import hudson.plugins.sonar.*
import hudson.plugins.sonar.model.TriggersConfig
import hudson.tools.*
import com.cloudbees.plugins.credentials.*
import com.cloudbees.plugins.credentials.impl.*
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

def credentials = getOrCreateCredentials(new UsernamePasswordCredentialsImpl(
    CredentialsScope.GLOBAL,
    "sonarqube", 
    "SonarQube API username and token - RESTART JENKINS ON CHANGE", 
    "devops-system", 
    ""  //just a default value, user should create in SonarQuybe and update post initial run
))

//TODO: check on timer if token is empty and try to create one using devops-system credentials
///sonarqube/api/user_tokens/generate POST name:test,login:devops-system => {"login":"devops-system","name":"test","token":"foobar"}

def instance = Jenkins.getInstance()
def plugins = instance.getPluginManager().getPlugins();
def config = instance.getDescriptor(SonarGlobalConfiguration.class)
def installation = new SonarInstallation(
    "sonarqube",
    "http://sonarqube:9000/sonarqube/",
    credentials.getPassword().toString(),     
    plugins.find({ it.getShortName() == "sonar" }).getVersion(),
    "",
    new TriggersConfig(),
    ""
)
config.setInstallations((SonarInstallation[]) [installation]);
config.save()

config = instance.getDescriptor("hudson.plugins.sonar.SonarRunnerInstallation")
installation = new SonarRunnerInstallation(
    "sonarscanner", 
    "", 
    [new InstallSourceProperty([new SonarRunnerInstaller("3.2.0.1227")])]
)
config.setInstallations((SonarRunnerInstallation[]) [installation])
config.save()

instance.save()