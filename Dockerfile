FROM ubuntu:latest AS build

RUN apt-get update
RUN apt-get install openjdk-17-jdk -y
COPY . .
CMD ["./gradlew", "clean", "bootJar"]

FROM openjdk:17-jdk

EXPOSE 8666

COPY --from=build /build/libs/TravelChat-plain.jar app.jar

ENTRYPOINT ["java", "-jar", "app.jar"]