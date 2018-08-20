// see https://github.com/jenkinsci/gitlab-plugin/pull/559

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

def gitlabPlugin = Jenkins.instance.getDescriptor("com.dabsquared.gitlabjenkins.connection.GitLabConnectionConfig")
def env = System.getenv()

global_domain = Domain.global()
credentials_store =
  Jenkins.instance.getExtensionList(
    'com.cloudbees.plugins.credentials.SystemCredentialsProvider'
  )[0].getStore()

def connections = new ArrayList<>();
def gitlabCredentials = new GitLabApiTokenImpl(
    CredentialsScope.GLOBAL,
    "gitlab",
    "gitlab API key for gitlab - ${env.GITLAB_URL}",
    new Secret(env.GITLAB_TOKEN)
)
def gitlabConfig = new GitLabConnection(
    "gitlab",
    env.GITLAB_URL,
    env.GITLAB_TOKEN,
    true,
    10,
    10
)

credentials_store.addCredentials(global_domain, gitlabCredentials)
connections.add(gitlabConfig)
gitlabPlugin.setConnections(connections)