#!/bin/sh

docker run \
	--rm --name prv-elassandra \
	-p 9043:9042 \
	-p 9201:9020 \
	-p 9301:9300 \
	-v $HOME/docker/volumes/elassandra:/var/lib/cassandra \
	strapdata/elassandra

	#-e CASSANDRA__storage_port=7002 \
	#-e CASSANDRA__ssl_storage_port=7003 \
#	-e CASSANDRA__native_transport_port=9043 \
#	-e CASSANDRA__rpc_port=9161 \
#	-e ELASTICSEARCH__http__port=9201 \
