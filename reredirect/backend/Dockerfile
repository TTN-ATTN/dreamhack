# Base image
FROM python:3.12-slim

# Working directory
WORKDIR /app

# Copy dependencies file
COPY requirements.txt .

# Install packages
RUN pip install --no-cache-dir -r requirements.txt

# Copy app code
COPY . .

# Expose port 80
EXPOSE 80

# Run app with Waitress
CMD ["waitress-serve", "--host=0.0.0.0", "--port=80", "wsgi:application"]