FROM eclipse-temurin:17-alpine AS builder
COPY . .
RUN chmod 755 ./gradlew
RUN ./gradlew buildFatJar

FROM eclipse-temurin:17-jre-alpine
COPY --from=builder ./build/libs/*.jar app.jar
COPY --from=builder ./server server
CMD ["java", "-jar", "app.jar"]