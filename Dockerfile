#===================#
# multi-stage build #
#===================#
FROM eclipse-temurin:17-jre as builder

# create & change working directory
WORKDIR /app

# copy the "fat jar" to current "/app" directory
ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} app.jar

# 1st stage: extract fat jar into layers
RUN java -Djarmode=layertools -jar app.jar extract

# prepare runtime
FROM eclipse-temurin:17-jre as runtime

# SECURITY: add a non-privileged linux user
RUN useradd -ms /bin/bash cloudchaser
USER cloudchaser

# script variables + env args, or secrets, etc
# (similarly done for python/java/node.js/go/etc)
ARG JAVA_OPTS
ENV JAVA_OPTS=$JAVA_OPTS
# expose ports to outside world
EXPOSE 8080

# 2nd stage : Copy the extracted layers
COPY --from=builder app/dependencies/ ./
COPY --from=builder app/spring-boot-loader ./
COPY --from=builder app/snapshot-dependencies/ ./
COPY --from=builder app/application/ ./
ENTRYPOINT exec java "org.springframework.boot.loader.JarLauncher"