FROM openjdk:21-jdk
ARG JAR_FILE=target/task-management-system-app-0.0.1-SNAPSHOT.jar
COPY ${JAR_FILE} /task-management-system-app/app.jar
ENTRYPOINT ["java","-jar","/task-management-system-app/app.jar"]