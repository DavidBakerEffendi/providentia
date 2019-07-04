#!/bin/sh

docker container start jce-elasticsearch
docker container start jce-cassandra
docker container start jce-janusgraph

