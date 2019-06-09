#!/bin/sh

docker run --rm   --name pg-docker -e POSTGRES_PASSWORD=docker -d -p 127.0.0.1:5432:5432 -v $HOME/docker/volumes/postgres:/var/lib/postgresql/data  postgres
