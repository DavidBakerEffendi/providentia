# Providentia-DB

The following are import tools to import the respective dataset to analyse into each of the back end storage solutions.

## Getting started
First normalize Yelp files in the `normalize-yelp` directory. Once business.json, review.json, and user.json have been normalized, navigate to the `import-tool` directory to start importing data into the various databases.

## Docker Instructions

### Cassandra

TODO: Note that JanusGraph-Cass docker compose also spins up a cassandra docker container

Source: [Five Minute Guide: Getting Started with Cassandra on Docker](https://medium.com/@michaeljpr/five-minute-guide-getting-started-with-cassandra-on-docker-4ef69c710d84)

1) Pull the DataStax Image
	This distribution contains Search Engine, Spark Analytics and Graph Components.
	```bash
	$ docker pull datastax/dse-server:latest
	```

2) Start the Docker container:
	* The -g flag starts a Node with Graph Model enabled
	* The -s flag starts a Node with Search Engine enabled
	* The -k flag starts a Node with Spark Analytics enabled
	```bash
	$ docker run -e DS_LICENSE=accept --memory 2g --name dse-docker -d datastax/dse-server -g -s -k
	```

3) Start the DataStax Studio container:
	The -p flag is for mapping ports between container and host. The 9091 port is the default address for Studio.
	```bash
	$ docker run -e DS_LICENSE=accept --link dse-docker -p 9091:9091 --memory 500m --name my-studio -d datastax/dse-studio
	```
	To visit the studio, connect to: http://localhost:9091/

### PostgreSQL

Source: [Don’t install Postgres. Docker pull Postgres](https://hackernoon.com/dont-install-postgres-docker-pull-postgres-bee20e200198)

1) Pull the PostgreSQL image:
	```bash
	docker pull postgres
	```

2) For data to persist
	```bash
	mkdir -p $HOME/docker/volumes/postgres
	```

3) Start postgres docker image
	POSTGRES_PASSWORD could/should be changed.
	POSTGRES_USER and POSTGRES_DB.POSTGRES_USER sets the superuser name. If not 
	provided, the superuser name defaults to 'postgres'.
	POSTGRES_DB sets the name of the default database to setup. If not provided
	it defaults to POSTGRES_USER.
	```bash
	docker run --rm   --name pg-docker -e POSTGRES_PASSWORD=docker -d -p 127.0.0.1:5432:5432 -v $HOME/docker/volumes/postgres:/var/lib/postgresql/data  postgres
	```

4) Connect to Postgres
	```bash
	docker exec -tiu postgres pg-docker psql
	```

