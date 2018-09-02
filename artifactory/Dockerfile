FROM docker.bintray.io/jfrog/artifactory-oss:6.3.0

USER root

RUN apt-get update && apt-get install -y \
    gettext-base

COPY config /config/
RUN chown -R artifactory:artifactory /config

COPY init.sh /init.sh
RUN chmod 755 /init.sh

USER artifactory

ENTRYPOINT [ "/init.sh" ]