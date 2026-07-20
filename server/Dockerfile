ARG VERSION=latest
FROM eclipse-temurin:17-jre
WORKDIR /app
COPY server/build/libs/server.jar app.jar

ENV PORT=8080
ENV HOST=0.0.0.0
EXPOSE 8080

LABEL version="${VERSION}"
ENTRYPOINT ["java", "-jar", "app.jar"]
