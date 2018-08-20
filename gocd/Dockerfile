FROM gocd/gocd-server:v18.6.0

RUN apk add gettext

COPY init.sh /docker-entrypoint.d/init.sh
RUN chmod +x /docker-entrypoint.d/init.sh

COPY cruise-config.xml /cruise-config.xml
