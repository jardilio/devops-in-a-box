# Overview

"DevOps in a Box" - A complete turn-key solution for accelerated DevOps toolchain deployment

* [Jenkins](./jenkins) - Solution for continuous integration
* [GitLab](./gitlab) - Solution for code repository, review and task management
* [Rocket.Chat](./rocketchat) - Solution for team communications
* [Hubot](./hubot) - Solution for chatbots interfaced through Rocket.Chat and integrated with other services
* [Artifactory](./artifactory) - Solution for managing build artifacts and dependencies
* [SonarQube](./sonarqube) - Solution for managing code quality
* [GoCD](./gocd) - Solution for continuous deployment
* [OpenLDAP](./openldap) - Solution for single LDAP authentication for all above services
* [NGINX](./proxy) - Solution for subdomain reverse proxy for all above services

*NOTE* More services in progress including: prometheus, grafana, ansible...

See each of the solution providers above for more information about the implementation and configuration

# Getting Started
<!--
<table style="background: black; color: #008000; font-family: 'Lucida Console', 'Courier New';">
    <tbody>
        <tr>
            <td><img src="./images/Step1.jpg"/></td>
            <td>$ git clone https://github.com/jardilio/devops-in-a-box.git</td>
            <td><img src="./images/Download.jpg"/></td>
        </tr>
        <tr>
            <td><img src="./images/Step2.jpg"/></td>
            <td>$ docker-compose build</td>
            <td><img src="./images/Build.jpg"/></td>
        </tr>
        <tr>
            <td><img src="./images/Step3.jpg"/></td>
            <td>$ docker-compose up</td>
            <td><img src="./images/Open.jpg"/></td>
        </tr>
    </tbody>
</table>
-->
<img src="./images/entire.jpg"/>

And thats the way you do it!

This may take a few minutes to download and build the dependencies the first time. Once complete, each
of the services should be running as configured in [docker-compose.yml](./docker-compose.yml) and accessible
at [http://localhost](http://localhost).

Note that there are many services and volumes that must initialize, it may take 
a bit after the initial build for all services to become available, check your console. You will get 502
errors from the proxy server until the service is ready and reachable.

If you want to speed up this process for testing and only work with a single named service, you can 
reduce the total number of loaded services and only load it and its dependencies, example:

```
docker-compose up jenkins
```

The [docker-compose.yml](./docker-compose.yml) file makes use of environment variables which
are defined in [.env](./.env) and is read in automatically by `docker-compose` when executed. 
These settings are also used to generate k8s configurations and should be set for the 
target environment you will deploy to.

## Initial Environment Setup

1. (Optional for local dev) Replace the default passwords 'P@ssw0rd' 
    * Update `.env` before building your new environment
    * Passwords can be updated at runtime in the environment from http://localhost/openldap/
    using `cn=admin,dc=devops` (or your currently configured dc) and current admin password
    * Note that some services require these passwords to be passed in as a secrete (ie for LDAP integration), if 
    these values are changed in the environment, then configurations for services need to be updated and 
    restarted to take effect.
2. Setup Gitlab admin account
    * Go to http://localhost/gitlab/
    * Login using `Standard` mode using `root` and `P@ssw0rd`
    * Go to http://localhost/gitlab/admin/hooks
    * Create a new hook to `http://jenkins:8080/jenkins/generic-webhook-trigger/invoke` using all triggers
    * (Optional for local dev) Go to http://localhost/gitlab/profile/password/edit and change your root password
    * Logout http://localhost/gitlab/users/sign_out 
3. Create new API token for Gitlab using `devops-system` user under `LDAP`
    * Go to http://localhost/gitlab/profile/personal_access_tokens
    * Use `devops` for the identity name and generate a new API key
    * Copy the generated API key for later
4. Setup SonQube admin account
    * Go to http://localhost/sonarqube
    * Login using `admin` as both username and password
    * Go to http://localhost/sonarqube/admin/settings?category=webhooks
    * Create a new hook to `http://jenkins:8080/jenkins/sonarqube-webhook/`
    * (Optional for local dev) Go to http://localhost/sonarqube/account/security/ and change your admin password
    * Logout 
4. Create new API token for SonarQube using `devops-system` user
    * Go to http://localhost/sonarqube/account/security/
    * Use `devops` for the identity name and generate a new API key
    * Copy the generated API key for later
5. Update credentials used by Jenkins using `devops-admin` user
    * Go to http://localhost/jenkins/credentials/store/system/domain/_/
    * Update the credential tokens for Gitlab and SonarQube with the values from steps 2 and 3
6. Restart Jenkins 
    * Go to http://localhost/jenkins/safeRestart
    * Confirm to restart
7. Update configurations used by Hubot using `devops-admin` user
    * Go to http://localhost/rocketchat/direct/devops.system
    * Type in `env set GITLAB_TOKEN [INSERT_COPIED_TOKEN]`
    * Type in `reload` for changes to take effect

## Starting a New Project

Create new source project repo in Gitlab with a Jenkinsfile

* Go to http://localhost/gitlab/projects/new using any user
    * Example, import from https://github.com/jardilio/express-app-testing-demo.git
* Project should optionally have a `jenkins.properties` and `sonar-project.properties` file (see example for reference)
* The `Jenkinsfile` in the reference example leverages convienence functions from the internal `devops` library (see example for reference)
* Go to the project `Settings` page in Gitlab, select `Members` and add `devops-system` as `master`
* Jenkins will be notified of the new project and create a matching job to build it. A webhook will also be created automatically to build when commits are pushed to the repo.


# Building and Deploying

* Set `DOCKER_IMAGE_TAG` with a version you want to tag the new build as (default is latest). Optionally you may want to set or change 
other values in [.env](./.env) as this will generate the defaults for k8s configurations.
* Log in with `docker login` to an account that has access to the destination repos specified by the image names in [docker-compose.yml](./docker-compose.yml) 
* Build the images using `docker-compose build`
* Push the images using `docker-compose push`

```
export DOCKER_IMAGE_TAG=2.0
docker login
docker-compose build
docker-compose push
```

Once the images have been built and pushed to the repos, you can deploy using one of the helper services in [deploy](./deploy). Make
sure that [.env](./.env) is updated for the target environment, this is used to build the default configuration files for kubernetes.

```
cd deploy
docker-compose run gcp
```