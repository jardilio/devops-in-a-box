import jenkins.model.JenkinsLocationConfiguration

def env = System.getenv()
def jlc = JenkinsLocationConfiguration.get()

jlc.setUrl(env.JENKINS_URL) 
jlc.save()