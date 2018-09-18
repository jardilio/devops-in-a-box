FROM jpetazzo/dind

ENV JENKINS_SWARM_VERSION 3.12
ENV JAVA_HOME /usr/lib/jvm/java-8-oracle

# Install Java.
RUN echo "deb http://ppa.launchpad.net/webupd8team/java/ubuntu trusty main" > /etc/apt/sources.list.d/webupd8team-java.list
RUN apt-key adv --keyserver keyserver.ubuntu.com --recv-keys EEA14886
RUN apt-get update

RUN \
  echo oracle-java8-installer shared/accepted-oracle-license-v1-1 select true | debconf-set-selections && \
  apt-get install -y oracle-java8-installer && \
  rm -rf /var/lib/apt/lists/* && \
  rm -rf /var/cache/oracle-jdk8-installer

RUN sudo curl -L https://github.com/docker/compose/releases/download/1.21.2/docker-compose-$(uname -s)-$(uname -m) -o /usr/local/bin/docker-compose
RUN chmod +x /usr/local/bin/docker-compose

RUN curl --create-dirs -sSLo /usr/share/jenkins/swarm-client.jar https://repo.jenkins-ci.org/releases/org/jenkins-ci/plugins/swarm-client/$JENKINS_SWARM_VERSION/swarm-client-$JENKINS_SWARM_VERSION.jar \
  && chmod 755 /usr/share/jenkins

RUN apt update && apt install -y git

ENTRYPOINT ["wrapdocker"]
CMD ["/usr/bin/java", "-jar", "/usr/share/jenkins/swarm-client.jar", "-master", "http://jenkins:8080/jenkins", "-username", "devops-system", "-passwordEnvVariable", "JENKINS_AGENT_PASSWORD", "-executors", "2", "-labels", "docker", "-name", "docker-agent", "-showHostName"]
#COPY startup.sh /
#CMD [ "/startup.sh" ]

VOLUME [ "/workspace" ]

RUN mkdir /etc/docker
RUN echo '{ "insecure-registries" : [ "artifactory:8081" ] }' > /etc/docker/daemon.json