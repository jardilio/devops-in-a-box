import jenkins.*
import hudson.*
import com.cloudbees.plugins.credentials.*
import com.cloudbees.plugins.credentials.common.*
import com.cloudbees.plugins.credentials.domains.*
import com.cloudbees.jenkins.plugins.sshcredentials.impl.*
import hudson.plugins.sshslaves.*;
import hudson.model.*
import jenkins.model.*
import org.apache.commons.lang.*
import hudson.security.*

def instance = Jenkins.getInstance()
def env = System.getenv()

String server = 'ldap://openldap'
String rootDN = env.LDAP_BASE_DN
String userSearchBase = ''
String userSearch = "(&(uid={0})(memberof=cn=devops-user,ou=roles,${env.LDAP_BASE_DN}))"
String groupSearchBase = ''
String managerDN = "cn=readonly,${env.LDAP_BASE_DN}"
String passcode = env.LDAP_READONLY_USER_PASSWORD
boolean inhibitInferRootDN = false

SecurityRealm realm = new LDAPSecurityRealm(server, rootDN, userSearchBase, userSearch, groupSearchBase, managerDN, passcode, inhibitInferRootDN) 
instance.setSecurityRealm(realm)
instance.save()