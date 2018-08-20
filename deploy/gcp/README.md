This will build the k8s configuration files and deploy them to an existing managed Kubernetes cluster in GCP. You will need to:

* Create a new Kubernetes cluster in GCP
* Update [.env](./env) with your account, zone, and cluster information
* Download your `key.json` file from GCP for the account that has access to `deploy/gcp/key.json`

Once this is done, you can simply run the command using docker-compose to build and deploy the environment to your cluster:

```
cd deploy
docker-compose run gcp
```