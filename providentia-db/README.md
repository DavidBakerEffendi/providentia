# Providentia-DB

The following three directories host tools and scripts to launch each database, preprocess and import the dataset.

## Getting started

The following is the order in which each directory should be visited:

* `docker-containers` will contain the Docker compose files to run each database.
* `normalize-dataset` will contain a Python script to preprocess the Yelp Challenge 2019 dataset.
* `import-tool` will import the dataset into JanusGraph and PostgreSQL. For importing to TigerGraph see `docker-containers/tigergraph`.

