FROM osixia/openldap:1.2.1

ENV LDAP_ADMIN_PASSWORD='P@ssw0rd'
ENV LDAP_CONFIG_PASSWORD='P@ssw0rd'
ENV LDAP_READONLY_USER="true"
ENV LDAP_READONLY_USER_USERNAME="readonly"

RUN rm -rf /container/service/slapd/assets/config/bootstrap/ldif/custom
RUN mkdir -p /container/service/slapd/assets/config/bootstrap/ldif/custom
COPY default-users-and-roles.ldif /container/service/slapd/assets/config/bootstrap/ldif/custom/
RUN ls /container/service/slapd/assets/config/bootstrap/ldif/custom/