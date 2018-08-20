# Overview

A default preconfigured ROCKET.CHAT instance for chat platform. This is an open source web chat platform built
 Based on [https://hub.docker.com/r/rocketchat/rocket.chat/](https://hub.docker.com/r/rocketchat/rocket.chat/).

# Configuration

## Users and Authentication

Users are centrally managed via [OpenLDAP](../openldap). Use the `devops-admin` user to login for the first time without any registration. And the first user whoever has logged in will be a defualt Administrator.

## Bot details

By defualt we ae configuring the bot with name 'devops' and user with 'devops-system' which can be modified by the environment variable BOT_NAME
ROCKETCHAT_USER in [Dockerfile](Dockerfile).

To test the bot functionality and supported features,the user should give the command in after logging into rokcetchat other than 'devops-system'
and give the command '@devops help' to list all other commands supported and configured.

All the commands will be addressed with bot name i.e '@devops'

For example,  '@devops jenkins list' will list all the jobs. From the listed jobs, we can run a build with the command '@devops jenkins build jobname'
