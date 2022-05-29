# vpwa-slack-spinoff

## Docker setup

- Added [Dockerfile](client/Dockerfile) and [.dockerignore](client/.dockerignore) for client (Quasar)

- Added [Dockerfile](server/Dockerfile) and [.dockerignore](server/.dockerignore) for server (AdonisJS)

- Added [Docker compose config](./docker-compose.yaml)

Build with `docker-compose build`
then run with `docker-compose up`
