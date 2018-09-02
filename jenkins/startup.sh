#!/bin/bash
PWORD=$(date +%s | sha256sum | base64 | head -c 32)
echo $PWORD
echo $PWORD > /shared_secrets/jenkins-agent.txt || echo "unable to write shared secrets to /shared_secrets!" && exit 1
cat /shared_secrets/jenkins-agent.txt
#/sbin/tini -- /usr/local/bin/jenkins.sh