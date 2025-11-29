FROM eclipse-temurin:11-jre

RUN groupadd -r appuser && useradd -r -g appuser appuser

COPY flag /flag
RUN chown root:appuser /flag && chmod 440 /flag

WORKDIR /app
COPY free-market-1.0.0.jar app.jar
RUN chown -R appuser:appuser /app

USER appuser

EXPOSE 8080
CMD ["java", "-jar", "app.jar"]
