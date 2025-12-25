FROM python:3.9-slim

RUN apt-get update && apt-get install -y \
    wget \
    gnupg \
    unzip \
    ca-certificates \
    chromium \
    chromium-driver \
    && apt-get clean \
    && rm -rf /var/lib/apt/lists/*

RUN pip install --no-cache-dir flask selenium

WORKDIR /app

COPY app.py .
COPY flag.txt .
COPY templates ./templates

EXPOSE 8000

CMD ["python", "app.py"]