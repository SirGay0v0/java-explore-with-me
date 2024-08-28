FROM amazoncorretto:21-alpine-jdk
ENV JAVA_TOOL_OPTIONS -agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=*:8081
VOLUME /tmp
COPY /target/ewm-main-service-0.0.1-SNAPSHOT.jar ewm-main-service.jar
ENTRYPOINT ["java", "-jar", "/ewm-main-service.jar"]