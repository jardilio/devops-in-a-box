import jenkins.model.*;
import com.dabsquared.gitlabjenkins.connection.GitLabConnection
import com.cloudbees.plugins.credentials.*
import com.cloudbees.plugins.credentials.common.*
import com.cloudbees.plugins.credentials.domains.*
import org.jenkinsci.plugins.plaincredentials.impl.*
import com.cloudbees.jenkins.plugins.sshcredentials.impl.*
import hudson.plugins.sshslaves.*
import org.apache.commons.fileupload.*
import org.apache.commons.fileupload.disk.*
import java.nio.file.Files
import com.dabsquared.gitlabjenkins.connection.GitLabApiTokenImpl
import hudson.util.Secret
import jenkins.security.*
import hudson.security.*
import hudson.model.*
import org.apache.commons.lang.*

def instance = Jenkins.getInstance()

def strategy = new GlobalMatrixAuthorizationStrategy()
strategy.add(Jenkins.READ,'authenticated')
strategy.add(Item.BUILD,'authenticated')
strategy.add(Item.READ,'authenticated')
strategy.add(Item.DISCOVER,'authenticated')
strategy.add(Item.CANCEL,'authenticated')
strategy.add(Item.CONFIGURE,'devops-admin')
strategy.add(Item.CONFIGURE,'devops-system')
strategy.add(Item.READ,'devops-admin')
strategy.add(Item.READ,'devops-system')
strategy.add(Item.READ,'anonymous')
strategy.add(Item.DISCOVER,'devops-system')
strategy.add(Item.DISCOVER,'devops-admin')
strategy.add(Item.CREATE,'devops-system')
strategy.add(Item.CREATE,'devops-admin')
strategy.add(Item.DELETE,'devops-system')
strategy.add(Item.DELETE,'devops-admin')
strategy.add(Jenkins.ADMINISTER, "devops-admin")
strategy.add(Computer.BUILD,'devops-system')
strategy.add(Computer.CONFIGURE,'devops-system')
strategy.add(Computer.CONNECT,'devops-system')
strategy.add(Computer.CREATE,'devops-system')
strategy.add(Computer.DISCONNECT,'devops-system')
instance.setAuthorizationStrategy(strategy)
instance.save()