#!/bin/bash

gcloud auth activate-service-account $GCP_ACCOUNT --key-file=$GCP_KEY_FILE
gcloud container clusters get-credentials $GCP_CLUSTER --zone $GCP_ZONE --project $GCP_PROJECT
kubectl apply -f /workspace/deploy/k8s