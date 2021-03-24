FROM maven:3.6.1-jdk-8-alpine AS MAVEN_BUILD
COPY ./ ./
RUN mvn clean package

FROM openjdk:8-jre-alpine3.9
# COPY target/sms-0.0.1-SNAPSHOT.jar /demo.jar
COPY --from=MAVEN_BUILD target/sms-0.0.1-SNAPSHOT.jar /demo.jar
CMD ["java", "-jar", "/demo.jar"]