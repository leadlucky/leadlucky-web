FROM openjdk:8-jdk-alpine
ARG JAR_FILE=build/libs/leadlucky-api-*.jar
ADD $JAR_FILE app.jar
ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom","-jar","/app.jar"]
