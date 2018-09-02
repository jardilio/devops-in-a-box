# Overview

"DevOps in a Box" - A complete turn-key solution for accelerated DevOps toolchain deployment

* [Jenkins](./jenkins) - Solution for continuous integration
* [GitLab](./gitlab) - Solution for code repository, review and task management
* [Rocket.Chat](./rocketchat) - Solution for team communications and chatbots
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

1. Replace the default passwords 'P@ssw0rd' in `.env` before building your new environment
    * Passwords can be updated at runtime in the environment from () 
    using `cn=admin,dc=devops` (or your currently configured dc) and current admin password
    * Note that some services require these passwords to be passed in as a secrete (ie for LDAP integration), if 
    these values are changed in the environment, then configurations for services need to be updated and 
    restarted to take effect.
2. Create new API token for Gitlab using `devops-system` user 
    * Go to http://localhost/gitlab/profile/personal_access_tokens
    * Use `jenkins` for the identity name and generate a new API key
    * Copy the generated API key for step 4
3. Create new API token for SonarQube using `devops-system` user
    * Go to http://localhost/sonarqube/account/security/
    * Use `jenkins` for the identity name and generate a new API key
    * Copy the generated API key for step 4
4. Update credentials used by Jenkins using `devops-admin` user
    * Go to http://localhost/jenkins/credentials/store/system/domain/_/
    * Update the credential tokens for Gitlab and SonarQube with the values from steps 2 and 3
5. Restart Jenkins 
    * Go to http://localhost/jenkins/safeRestart
    * Confirm to restart

## Starting a New Project
1. Create new source project repo in Gitlab with a Jenkinsfile
    * Go to http://localhost/gitlab/projects/new
    * Example, import from https://github.com/jardilio/express-app-testing-demo.git
    * Project should optionally have a `jenkins.properties` and `sonar-project.properties` file (see example for reference)
    * The `Jenkinsfile` in the reference example leverages convienence functions from the internal `doiab` library
2. Create new artifact repository in Artifactory 
    * Go to http://localhost/artifactory/webapp/#/admin/repository/local/new
    * Select the appropriate project type (ie generic) 
    * Provide a name, when using the referecen pipeline, name should match the `app.id` from `jenkins.properties`, by 
    default, this value is the name of the jenkins job if not provided.
3. Create new multibranch pipeline job in Jenkins 
    * Go to http://localhost/jenkins/view/all/newJob
    * Provide a name and select multibranch pipeline
    * Use your new project repo URL from Gitlab as the source
    * Use the Gitlab credentials from the dropdown
    * Save and run the new job


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