#!/bin/sh

if [ ! /godata/config -d ]; then 
    mkdir /godata/config
fi

# copy over our configuration defaults
envsubst < /cruise-config.xml > /godata/config/cruise-config.xml
chown -R go:go /godata/config