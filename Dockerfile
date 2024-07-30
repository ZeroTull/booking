# Stage 1: Build the application
FROM maven AS build
WORKDIR /app
COPY * /app
RUN mvn package -DskipTests



FROM openjdk
WORKDIR /app
COPY --from=build /app/target/booking-0.0.1-SNAPSHOT.jar /app/booking-0.0.1-SNAPSHOT.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "booking-0.0.1-SNAPSHOT.jar"]