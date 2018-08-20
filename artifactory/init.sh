#!/bin/bash

set -e

# copy over our configuration defaults
mkdir -p /var/opt/jfrog/artifactory/etc
envsubst < /config/artifactory.config.import.xml > /var/opt/jfrog/artifactory/etc/artifactory.config.import.xml
cp /var/opt/jfrog/artifactory/etc/artifactory.config.import.xml /var/opt/jfrog/artifactory/etc/artifactory.config.bootstrap.xml
cp /var/opt/jfrog/artifactory/etc/artifactory.config.import.xml /var/opt/jfrog/artifactory/etc/artifactory.config.xml
cp /config/security.import.xml /var/opt/jfrog/artifactory/etc/security.import.xml
cp /config/security.import.xml /var/opt/jfrog/artifactory/etc/security.xml
cp /config/artifactory.system.properties /var/opt/jfrog/artifactory/etc/artifactory.system.properties

/entrypoint-artifactory.sh