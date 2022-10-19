FROM eclipse-temurin:17

# SECURITY: add a non-privileged linux user for installing/running the app
RUN useradd -ms /bin/bash cloudchaser
USER cloudchaser

# create & change working directory
WORKDIR /app

# optional args input
ARG JAVA_OPTS
ENV JAVA_OPTS=$JAVA_OPTS

# copy the "fat jar" to current "/app" directory
COPY target/*.jar app.jar

# expose ports to outside world
EXPOSE 8080

# start the app
ENTRYPOINT exec java $JAVA_OPTS -jar app.jar
