# Build stage
FROM maven:3.9.8-eclipse-temurin-17 AS build
WORKDIR /app

# Optional proxy for Maven dependency download
ARG HTTP_PROXY
ARG HTTPS_PROXY
ARG NO_PROXY

ENV HTTP_PROXY=$HTTP_PROXY \
    HTTPS_PROXY=$HTTPS_PROXY \
    NO_PROXY=$NO_PROXY \
    http_proxy=$HTTP_PROXY \
    https_proxy=$HTTPS_PROXY \
    no_proxy=$NO_PROXY

COPY pom.xml .
RUN mvn -q -e -DskipTests dependency:go-offline
COPY src ./src

# Build jar and copy it to a stable name (avoids version/name mismatches)
RUN mvn -q -e -DskipTests package \
 && JAR="$(ls -1 target/*.jar | grep -v '\.original$' | head -n 1)" \
 && echo "Using jar: ${JAR}" \
 && cp "${JAR}" /app/app.jar

# Run stage
FROM eclipse-temurin:17-jre
WORKDIR /app
COPY --from=build /app/app.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java","-jar","/app/app.jar"]
