#!/bin/sh

docker run --rm --name prv-docker -e POSTGRES_PASSWORD=docker -p 127.0.0.1:5432:5432 -v $HOME/docker/volumes/postgis:/var/lib/postgresql/data  mdillon/postgis