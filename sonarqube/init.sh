#!/bin/bash

# copy over our configuration defaults
envsubst < /opt/sonarqube/conf/sonar.properties > /opt/sonarqube/conf/sonar.properties

./bin/run.sh