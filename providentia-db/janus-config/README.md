# JanusGraph Config

The following file contains the configuration necessary for the bare-metal Gremlin server. `conf` should simply replace the exisiting `conf` in janusgraph-$VERSION-hadoop2. All this does is change the `.properties` file that is loaded to `providentia.properties`. `providentia.properties` is a basic CQL-ES configuration pointing to a keyspace called Yelp so that any remote connection to the Gremlin server will load up the Yelp graph.
