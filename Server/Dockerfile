FROM gradle:8.12.0-jdk23 AS build

WORKDIR /app

COPY . .

# Build the application
RUN gradle assemble

FROM openjdk:23-jdk

WORKDIR /app

COPY --from=build /app/build/libs/DrawLing-0.0.1-SNAPSHOT.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]
