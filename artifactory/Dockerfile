FROM docker.bintray.io/jfrog/artifactory-oss:6.1.0

RUN apt-get update && apt-get install -y \
    gettext-base

COPY config /config/
COPY init.sh /init.sh
RUN chmod 755 /init.sh

ENTRYPOINT [ "/init.sh" ]