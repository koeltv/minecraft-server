#FROM eclipse-temurin:17-alpine AS builder
#COPY . .
#RUN chmod 755 ./gradlew
#RUN ./gradlew runtime

FROM eclipse-temurin:17-jre
#COPY --from=builder ./build/minecraft-server-0.0.1/minecraft-server-alpine-linux /minecraft-server
COPY ./build/image/minecraft-server-linux /minecraft-server
WORKDIR /minecraft-server
CMD ["./minecraft-server"]