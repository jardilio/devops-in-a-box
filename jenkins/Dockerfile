FROM jenkins/jenkins:2.130

ENV JAVA_OPTS -Djenkins.install.runSetupWizard=false
ENV JENKINS_OPTS=--prefix=/jenkins

USER root

COPY plugins.txt /tmp/plugins.txt
RUN /usr/local/bin/install-plugins.sh < /tmp/plugins.txt

COPY . /tmp
# jenkins will only overwrite files that end with .override
RUN mkdir /usr/share/jenkins/ref/jobs
RUN for d in $(find /tmp/*/** -type d); do mkdir -- "/usr/share/jenkins/ref$(echo $d | sed -e 's/^\/tmp//g')" || echo "/usr/share/jenkins/ref$(echo $d | sed -e 's/^\/tmp//g') already exists"; done
RUN for f in $(find /tmp/*/** -type f); do cp -- "$f" "/usr/share/jenkins/ref$(echo $f | sed -e 's/^\/tmp//g').override"; done
RUN chown -R 1000:1000 /usr/share/jenkins/ref

RUN adduser jenkins root
RUN exec su -l jenkins

USER jenkins