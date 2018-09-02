#!/bin/bash

set -e

# copy over our configuration defaults
envsubst < /opt/sonarqube/conf/sonar.properties > /opt/sonarqube/conf/sonar.properties.tmp
mv -f /opt/sonarqube/conf/sonar.properties.tmp /opt/sonarqube/conf/sonar.properties

./bin/run.sh