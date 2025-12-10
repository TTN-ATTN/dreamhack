# Base image
FROM python:3.12-slim

# Environment variables
ENV PYTHONUNBUFFERED=1

# Create working directory
WORKDIR /app

# Copy dependencies
COPY requirements.txt .
RUN pip install --no-cache-dir -r requirements.txt

# Copy app code and templates
COPY . .

# Flask default port
EXPOSE 81

# Run Flask when container starts
CMD ["python", "app.py"]