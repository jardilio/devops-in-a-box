#!/bin/bash

rm -rf /workspace/deploy/k8s/*.yaml

# TODO: need to not override existing environment variables already set/exported
export $(cat /workspace/.env | xargs) && \
export NGINX_RESOLVER=kube-dns.kube-system.svc.cluster.local && \
export NGINX_SERVICE_SUFFIX=.default.svc.cluster.local && \
kompose -f /workspace/docker-compose.yml convert

if [ $DOCKER_IMAGE_TAG == "latest" ]
then
    echo "WARNING: forcing image pull every deployment when using '$DOCKER_IMAGE_TAG' tag for development purposes"
    for DEPLOYMENT in $(find /workspace/deploy/k8s/*-deployment.yaml -type f)
    do
        sed -i \
            -e 's/image:/imagePullPolicy: Always\n        image:/g' \
            -e 's/creationTimestamp: null/creationTimestamp: '"$(date -u +"%Y-%m-%dT%H:%M:%SZ")"'/g' \
            $DEPLOYMENT
    done
fi

# no kompose mapping, for services running non-root, need to set securityContexts for user
sed -i -e 's/containers:/securityContext:\n        fsGroup: 1000\n      containers:/g' /workspace/deploy/k8s/jenkins-deployment.yaml
sed -i -e 's/containers:/securityContext:\n        fsGroup: 1030\n      containers:/g' /workspace/deploy/k8s/artifactory-deployment.yaml

# fix until https://github.com/kubernetes/kompose/issues/1046, shared volume between master and agents
#sed -i -e 's/ReadWriteOnce/ReadWriteMany/g' /workspace/deploy/k8s/jenkins-persistentvolumeclaim.yaml