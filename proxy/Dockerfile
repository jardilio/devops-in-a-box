FROM nginx

RUN apt-get update && apt-get install -y \
    curl

COPY nginx.conf /etc/nginx/conf.d/devops.template
COPY html /usr/share/nginx/html
RUN chmod -R 755 /usr/share/nginx/html/

CMD /bin/bash -c "envsubst '\$NGINX_SERVER_NAME \$NGINX_PORT \$NGINX_RESOLVER \$NGINX_SERVICE_SUFFIX' < /etc/nginx/conf.d/devops.template > /etc/nginx/nginx.conf && cat /etc/nginx/nginx.conf && exec nginx -g 'daemon off;'"