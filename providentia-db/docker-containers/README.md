# Docker Containers

The following directory contains example scripts (especially useful for development) to pull/compose/run Docker containers hosting each of the databases. They all have a modular design where the scripts are meant to simplify the setting of various flags when starting containers such as Elassandra or making use of docker-compose for JanusGraph.

## Instructions for each of the databases

### JanusGraph

The JanusGraph docker image is built from a 3 stage docker-compose; JanusGraph, Cassandra and ElasticSearch.
* `janusgraph/docker-compose.yml`: The docker-compose for the JanusGraph/Cassandra/ElasticSearch container.
* `janusgraph/gremlin.sh`: Grants access to the Gremlin shell in the JanusGraph container.
* `janusgraph/cql.sh`: Grants access to the CQL shell in the Cassandra container.

### PostgreSQL 

The Postgres docker container is built from the official PostgreSQL image with PostGIS extension (`mdillon/postgist`).
* `postgis/docker-compose.yml`: The docker-compose for the PostGIS container.
* `postgis/sql.sh`: Grants access to the SQL shell in the Postgres container.

### Elassandra 

The Elassandra docker container is build from the official Strapdata Elassandra image.
* `elassandra/pull.sh`: Pulls the Elassandra image.
* `elassandra/start.sh`: Creates and runs an Elassandra container. See the script for configurations to edit.
* `elassandra/sql.sh`: Grants access to the CQL shell in the Cassandra container.
