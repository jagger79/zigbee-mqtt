FROM eclipse-temurin:25

COPY target/*-uber.jar /app.jar

USER 0

ENTRYPOINT ["java", "-jar", "/app.jar"]
