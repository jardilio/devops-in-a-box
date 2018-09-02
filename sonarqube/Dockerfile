FROM sonarqube:6.7

RUN apt-get update && apt-get install -y \
    gettext-base

COPY init.sh /init.sh
RUN chmod +x /init.sh

COPY conf /opt/sonarqube/conf/
COPY plugins /opt/sonarqube/extensions/plugins/

CMD ["/init.sh"]

ENV SONARQUBE_JDBC_USERNAME=sonar
ENV SONARQUBE_JDBC_URL=jdbc:postgresql://sonarqube-database:5432/sonar