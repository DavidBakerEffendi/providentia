FROM python:3.7.3-alpine

WORKDIR /app

COPY . .

RUN ["apk", "update"]
RUN ["apk", "add", "postgresql-dev", "gcc", "python3-dev", "musl-dev", "linux-headers"]

RUN ["pip3", "install", "--upgrade", "pip"]
RUN ["pip3", "install", "-r", "requirements.txt"]
RUN ["pip3", "install", "python-dotenv"]

CMD flask run --no-reload --host 0.0.0.0
