# syntax=docker/dockerfile:1
# Stage 1: Build the application
FROM maven:3.9.8-eclipse-temurin-21 AS build
WORKDIR /app

# unified-verificator (app/pom.xml) resolves from GitHub Packages -- needs auth even for
# public packages. Token passed via BuildKit secret mount, not ARG: ARG values persist in
# image layer history even for intermediate stages; secret mounts do not.
ARG GITHUB_ACTOR
RUN --mount=type=secret,id=github_token \
    mkdir -p /root/.m2 && printf '%s\n' \
    '<settings>' \
    '  <servers>' \
    '    <server>' \
    '      <id>github</id>' \
    "      <username>${GITHUB_ACTOR}</username>" \
    "      <password>$(cat /run/secrets/github_token)</password>" \
    '    </server>' \
    '  </servers>' \
    '</settings>' > /root/.m2/settings.xml

COPY app /app
RUN mvn dependency:go-offline
RUN mvn package -DskipTests

FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=build /app/target/booking-0.0.1-SNAPSHOT.jar /app/booking-0.0.1-SNAPSHOT.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "booking-0.0.1-SNAPSHOT.jar"]
