#!/bin/sh

# Obtain the schema from the schema file
users=$(cat ./elassandra-schema/users.json)
business=$(cat ./elassandra-schema/business.json)
review=$(cat ./elassandra-schema/review.json)
# Remove existing index
docker exec -it prv-elassandra curl -XDELETE http://127.0.0.1:9200/yelp-use?pretty=true
docker exec -it prv-elassandra curl -XDELETE http://127.0.0.1:9200/yelp-bus?pretty=true
docker exec -it prv-elassandra curl -XDELETE http://127.0.0.1:9200/yelp-rev?pretty=true
# Create indexes
docker exec -it prv-elassandra curl -XPUT http://127.0.0.1:9200/yelp-use
docker exec -it prv-elassandra curl -XPUT http://127.0.0.1:9200/yelp-bus
docker exec -it prv-elassandra curl -XPUT http://127.0.0.1:9200/yelp-rev
# Post the schema to ElasticSearch
docker exec -it prv-elassandra curl -XPUT -H 'Content-Type: application/json' -d "$users" http://127.0.0.1:9200/yelp-use/_mapping/users?pretty=true
docker exec -it prv-elassandra curl -XPUT -H 'Content-Type: application/json' -d "$business" http://127.0.0.1:9200/yelp-bus/_mapping/business?pretty=true
docker exec -it prv-elassandra curl -XPUT -H 'Content-Type: application/json' -d "$review" http://127.0.0.1:9200/yelp-rev/_mapping/review?pretty=true
