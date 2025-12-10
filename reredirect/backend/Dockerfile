# 베이스 이미지
FROM python:3.12-slim

# 작업 디렉토리
WORKDIR /app

# 종속성 파일 복사
COPY requirements.txt .

# 패키지 설치
RUN pip install --no-cache-dir -r requirements.txt

# 앱 코드 복사
COPY . .

# 81번 포트 노출
EXPOSE 80

# Waitress로 앱 실행
CMD ["waitress-serve", "--host=0.0.0.0", "--port=80", "wsgi:application"]