FROM openjdk:17
COPY target/*.jar gateway.jar
EXPOSE 8080
ENV JAVA_TOOL_OPTIONS -agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=*:8081
ENTRYPOINT ["java","-jar","/gateway.jar"]