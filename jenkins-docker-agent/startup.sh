#!/bin/bash

PWORDFILE=/var/jenkins_home/secrets/jenkins-agent.txt

while [ ! -f $PWORDFILE ] ;
do
    echo "waiting on jenkins master to create $PWORDFILE"
    sleep 10
done

/usr/bin/java \
    -jar /usr/share/jenkins/swarm-client.jar \
    -master http://jenkins:8080/jenkins \
    -username devops-system \
    -passwordFile $PWORDFILE \
    -labels docker \
    -name docker-agent \
    -executors 2 \
    -showHostName