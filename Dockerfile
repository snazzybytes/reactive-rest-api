FROM eclipse-temurin:17
VOLUME /tmp
ARG JAVA_OPTS
ENV JAVA_OPTS=$JAVA_OPTS
COPY target/reactive-rest-api-0.0.1-SNAPSHOT.jar reactiverestapi.jar
EXPOSE 8080
ENTRYPOINT exec java $JAVA_OPTS -jar reactiverestapi.jar
