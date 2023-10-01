FROM maven:3.8.4-openjdk-17-slim AS build
COPY src /home/app/src
COPY pom.xml /home/app
RUN mvn clean package -f /home/app/pom.xml -DskipTests -Dcheckstyle.skip

FROM openjdk:17.0.1-jdk-slim
COPY --from=build /home/app/target/*.jar /usr/local/lib/app.jar
ENTRYPOINT java -jar /usr/local/lib/app.jar