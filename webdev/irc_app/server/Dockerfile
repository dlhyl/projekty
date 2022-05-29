# Include the latest node image
FROM node:lts
# Aliases setup for container folders
ARG SERVER="/slek-server"
ARG SERVER_src="."
ARG BUILD="/slek-server/build"
ENV PORT="3333"
ENV HOST="0.0.0.0"
ENV NODE_ENV="development"
ENV APP_KEY="dLH-pVw1s8mHLq-XIOB5czKNgj3O-GCR"
ENV DRIVE_DISK="local"
ENV DB_CONNECTION="pg"
ENV PG_HOST="pg"
ENV PG_PORT="5432"
ENV PG_USER="postgres"
ENV PG_PASSWORD="postgres"
ENV PG_DB_NAME="slack"
# Set the working directory inside the container to server module
WORKDIR ${SERVER}
# Expose port outside container
EXPOSE ${PORT}
# Copy server module
COPY ${SERVER_src} ${SERVER}
# Build TS files
RUN npm install
RUN node ace build --production --ignore-ts-errors
# Update workdir
WORKDIR ${BUILD}
# Install production dependencies
# Start server module inside the container
CMD ["node", "server.js"]
