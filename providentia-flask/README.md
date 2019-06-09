# Providentia Flask

## Install

### Providentia

Create a virtualenv and activate it:
```bash
python3 -m venv venv
. venv/bin/activate
```
Install Providentia
```bash
pip install -e .
```

### Postgres Docker
```bash
docker pull postgres
mkdir -p $HOME/docker/volumes/postgres
docker run --rm   --name pg-docker -e POSTGRES_PASSWORD=docker -d -p 127.0.0.1:5432:5432 -v $HOME/docker/volumes/postgres:/var/lib/postgresql/data  postgres
docker exec -tiu postgres pg-docker psql
```

## Run
```bash
export FLASK_APP=flaskr
export FLASK_ENV=development
flask init-db 
flask run --no-reload
```
Open http://127.0.0.1:5000 in a browser.