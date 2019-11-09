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

### TigerGraph 

The TigerGraph docker container is build from the official TigerGraph image. Original instructions can be found [here](https://github.com/tigergraph/ecosys/blob/master/guru_scripts/docker/README.md).
* `tigergraph/docker-compose.yml`: The docker-compose for the TigerGraph container. This container does not give output.
TigerGraph is a bit more advanced in terms of accessing GSQL shell and the Graph Studio admin panel. Cheatsheet from the above source is below:
* Open a shell to the TigerGraph server using the following command: `ssh -p 14022 tigergraph@localhost` with password `tigergraph`.
* Start the TigerGraph service under bash shell (may take up to 1 minute) using `gadmin start`.

Now one will have access to the following:
* `gsql`: Grants access to GSQL shell.
* TigerGraph Visual IDE: Connect to `http://localhost:14240` for TigerGraph's Graph Studio.

#### Enabling Geospatial Functions

To enable use of geospatial functions, you need to copy the `hpp` and `cpp` files found in `tigergraph`. There is a bash script that uses `docker cp` to do this for you, all you need to to is run `./transfer_geofunctions` in the `tigergraph` directory. 

#### Importing Yelp Dataset

Once the dataset has been normalized and converted to CSV (See `../normalize-dataset`) then run `./transfer_data` which will move these CSV files and `loader.gsql` into the Docker container. Once SSH'd in to the Docker container, run `gsql` then the following command:
```
GSQL > @loader.gsql
```
This will begin the batch offline data loading job. Remember to edit the file paths in the script before starting each job.

### Elassandra (Deprecated)

The Elassandra docker container is build from the official Strapdata Elassandra image.
* `elassandra/pull.sh`: Pulls the Elassandra image.
* `elassandra/start.sh`: Creates and runs an Elassandra container. See the script for configurations to edit.
* `elassandra/sql.sh`: Grants access to the CQL shell in the Cassandra container.
