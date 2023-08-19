FROM eclipse-temurin:17-jdk-alpine as build
WORKDIR /workspace/app

COPY . /workspace/app

COPY gradlew .
COPY .gradle .gradle
COPY build.gradle .
COPY src src

# Run Gradle build and skip tests
RUN ./gradlew buildJar

# Create a directory for extracted dependencies and extract JARs
RUN mkdir -p build/dependency && (cd build/dependency; jar -xf ../build/libs/*.jar)

RUN ./gradlew bootJar

COPY build/travelchat.jar app.jar
EXPOSE 8666
ENTRYPOINT ["java","-jar","/app.jar"]
