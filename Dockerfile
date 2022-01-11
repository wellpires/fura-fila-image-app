FROM openjdk:8-jdk-alpine
ARG JAR_FILE=target/*.jar
WORKDIR /home/wellington/git-pessoal/fura-fila-image-app/
COPY ${JAR_FILE} app.jar
ENTRYPOINT ["java", "-Dspring.profiles.active=stg", "-jar", "app.jar"]

