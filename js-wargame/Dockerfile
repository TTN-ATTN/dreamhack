FROM eclipse-temurin:17-jdk-jammy AS build
WORKDIR /build
COPY java/Swing.java /build/Swing.java
RUN javac Swing.java && jar --create --file swing.jar --main-class Swing Swing.class

FROM python:3.13-slim
RUN apt-get update \
 && apt-get install -y --no-install-recommends \
      default-jre-headless ca-certificates \
      fontconfig fonts-dejavu-core \
      libharfbuzz0b libfreetype6 \
 && rm -rf /var/lib/apt/lists/*

WORKDIR /app
COPY --from=build /build/swing.jar /app/swing.jar
COPY app.py requirements.txt /app/
COPY templates /app/templates
RUN pip install --no-cache-dir -r requirements.txt

ENV PORT=8080
ENV JAR_PATH=/app/swing.jar

EXPOSE 8080
CMD ["python", "app.py"]
