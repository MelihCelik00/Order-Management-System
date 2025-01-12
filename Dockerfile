FROM eclipse-temurin:21-jdk-alpine

WORKDIR /app

COPY gradlew .
COPY gradle gradle
COPY build.gradle .
COPY settings.gradle .
COPY src src

RUN chmod +x ./gradlew
RUN ./gradlew clean build -x test

EXPOSE 8080

CMD ["java", "-jar", "build/libs/order-management-system-0.0.1-SNAPSHOT.jar"] 