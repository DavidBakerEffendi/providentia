# Providentia Flask

## Install

### Providentia

Create a virtualenv and activate it:
```bash
python3 -m venv venv
. venv/bin/activate
```
Set up Providentia requirements and environment variables
```bash
pip3 install -r requirements.txt
pip3 install python-dotenv
```

### Database
See `../providentia-db/docker-containers` for instructions on how to start the PostGIS docker container.
The analytics are stored in the `providentia` database - details of which can be seen in the `./db` 
directory. 

## Run
```bash
flask init-db 
flask run --no-reload
```
