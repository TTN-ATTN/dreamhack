FROM python:3.13.4-alpine

ENV MYSQL_HOST=mysql
ENV MYSQL_USER=user
ENV MYSQL_PASSWORD=password
ENV MYSQL_DATABASE=chall

WORKDIR /app

COPY requirements.txt .
RUN pip install -r requirements.txt
COPY app/ .

EXPOSE 5000

CMD ["python","app.py"]