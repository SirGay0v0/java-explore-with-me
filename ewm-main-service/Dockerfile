FROM amazoncorretto:21-alpine-jdk
VOLUME /tmp
COPY /target/ewm-main-service-0.0.1-SNAPSHOT.jar ewm-main-service.jar
ENTRYPOINT ["java", "-jar", "/ewm-main-service.jar"]