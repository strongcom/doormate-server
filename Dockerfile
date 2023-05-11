FROM openjdk:11
ENV TZ=Asia/Seoul
ARG JAR_FILE=doormate/target/*.jar
COPY ${JAR_FILE} app.jar
ENTRYPOINT ["java","-jar","/app.jar"]
