FROM amazoncorretto:21-alpine-jdk
ENV JAVA_TOOL_OPTIONS -agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=*:9091
COPY target/stats-server-0.0.1-SNAPSHOT.jar stats-server.jar
ENTRYPOINT ["java", "-jar", "/stats-server.jar"]