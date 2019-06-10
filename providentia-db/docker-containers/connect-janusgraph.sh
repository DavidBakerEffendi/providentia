#!/bin/sh

docker run --rm --link jce-janusgraph:janusgraph -e GREMLIN_REMOTE_HOSTS=janusgraph -it --net janusgraph-docker_jce-network janusgraph/janusgraph:latest ./bin/gremlin.sh
