# Providentia Flask

## Configuration

Look to `config.py` for a template of the configuration file. One needs to create a `instance/config.py` which will have the dev or prod specific configurations. Examples of dev or prod configurations will be seen commented out in `config.py`.

## Running Flask Backend

The following steps show how to run the Flask backend baremetal. To run this in Docker refer to `../docker-compose.yml`.

### Install

#### Providentia

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

#### Database
See `../providentia-db/docker-containers` for instructions on how to start the PostGIS docker container.
The analytics are stored in the `providentia` database - details of which can be seen in the `./db` 
directory. 

### Run
```bash
flask run --no-reload
```
