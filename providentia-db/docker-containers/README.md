# Docker Containers

The following directory contains example scripts (especially useful for development) to pull/compose/run Docker containers hosting each of the databases.

## JanusGraph & Cassandra

`start-janusgraph.sh` composes the `docker-compose-sql-es.yml` that starts a JanusGraph-Cassandra-ElasticSearch network (named `jce-network`). The Cassandra docker on this network is re-used for the pure Cassandra tests.

## PostgreSQL 

For the first time, run `pull-postgres.sh` to obtain the image and create a volume to persist the data on. One completed, run `start-postgres.sh` to start the image.
