FROM openjdk:11
ARG JAR_FILE=doormate/target/*.jar
COPY ${JAR_FILE} app.jar
RUN apk add tzdata && ln -snf /usr/share/zoneinfo/Asia/Seoul /etc/localtime
ENTRYPOINT ["java","-jar","/app.jar"]
