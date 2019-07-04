# Providentia-DB

The following are import tools to import the respective dataset to analyse into each of the back end storage solutions.

## Getting started

First normalize Yelp files in the `normalize-yelp` directory. Once business.json, review.json, and user.json have been normalized, navigate to the `import-tool` directory to start importing data into the various databases.

## Docker Instructions

Inside of the `docker-containers` directory you will find bash scripts pulling and starting the databases. Example, to pull Elassandra image (from this directory):
```bash
./docker-containers/elassandra/pull.sh
```
For more information, find the README in the `docker-containers` directory.
