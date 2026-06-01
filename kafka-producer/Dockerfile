FROM eclipse-temurin:21-jdk-alpline AS builder

WORKDIR /app

COPY pom.xml ./pom.xml

RUN apk add --no-cache maven && mvn dependency:go-offline -pl kafka-producer -am

COPY src ./src

RUN mvn clean package -DskipTests

FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

RUN addgroup -S appgroup && adduser -S appuser -G appgroup
USER appuser

COPY ./target/*.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", \
            "-XX:+UseContainerSupport", \
            "-XX:MaxRAMPercentage=75.0", \
            "-Djava.security.egd=file:/dev/./urandom", \
            "-jar", "app.jar"]