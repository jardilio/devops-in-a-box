FROM rocketchat/hubot-rocketchat:v1.0.11

ENV RESPOND_TO_DM=true
ENV ROCKETCHAT_ROOM=
ENV LISTEN_ON_ALL_PUBLIC=false
ENV ROCKETCHAT_URL=http://rocketchat:3000/rocketchat
ENV ROCKETCHAT_USER=devops-system
#ENV ROCKETCHAT_PASSWORD=
ENV ROCKETCHAT_AUTH=ldap
ENV BOT_NAME=devops
ENV HUBOT_ALIAS=devops
ENV HUBOT_LOG_LEVEL=debug

ENV HUBOT_JENKINS_URL=http://jenkins:8080/jenkins
ENV HUBOT_JENKINS_AUTH=devops-system:P@ssw0rd

ENV HUBOT_GITLAB_URL=http://gitlab:80/gitlab
#ENV HUBOT_GITLAB_ACCESSKEY=

ENV EXTERNAL_SCRIPTS=hubot-help,hubot-jenkins,hubot-reload-scripts
#,hubot-gitlab

VOLUME [ "/home/hubot/scripts/config" ]

USER root
COPY scripts/* /home/hubot/scripts/
RUN rm -rf /home/hubot/scripts/setenv.js
RUN chown -R hubot:hubot /home/hubot/scripts
#permissions issue with hubot and config
#USER hubot