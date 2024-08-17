FROM amazoncorretto:21-alpine-jdk
COPY target/stats-server-0.0.1-SNAPSHOT.jar stats-server.jar
ENTRYPOINT ["java", "-jar", "/stats-server.jar"]