FROM node:8

RUN apt-get update && apt-get install -y \
    libldap2-dev

WORKDIR /workspace
COPY package.json /workspace/
RUN npm install
COPY . /workspace/
CMD ["node", "./index.js"]

EXPOSE 3000

ENV SMTP_HOST=smtp
ENV SMTP_PORT=25