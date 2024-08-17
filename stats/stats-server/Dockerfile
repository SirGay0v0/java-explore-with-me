FROM amazoncorretto:21-alpine-jdk
VOLUME /tmp
COPY target/stats-server-0.0.1-SNAPSHOT.jar stats-server.jar
ENTRYPOINT ["java", "-jar", "/stats-server.jar"]