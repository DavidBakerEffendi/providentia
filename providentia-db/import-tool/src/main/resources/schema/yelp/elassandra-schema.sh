#!/bin/sh

# Obtain the schema from the mappings file
users=$(cat ./elassandra-mappings/users.json)
business=$(cat ./elassandra-mappings/business.json)
review=$(cat ./elassandra-mappings/review.json)
# Remove existing index
docker exec -it prv-elassandra curl -XDELETE http://127.0.0.1:9200/user-reviews?pretty=true
docker exec -it prv-elassandra curl -XDELETE http://127.0.0.1:9200/business-locations?pretty=true
docker exec -it prv-elassandra curl -XDELETE http://127.0.0.1:9200/review-times?pretty=true
# Create indexes
docker exec -it prv-elassandra curl -XPUT http://127.0.0.1:9200/user-reviews?pretty=true
docker exec -it prv-elassandra curl -XPUT http://127.0.0.1:9200/business-locations?pretty=true
docker exec -it prv-elassandra curl -XPUT http://127.0.0.1:9200/review-times?pretty=true
# Post the schema to ElasticSearch
docker exec -it prv-elassandra curl -XPUT -H 'Content-Type: application/json' -d "$users" http://127.0.0.1:9200/user-reviews/_mapping/users?pretty=true
docker exec -it prv-elassandra curl -XPUT -H 'Content-Type: application/json' -d "$business" http://127.0.0.1:9200/business-locations/_mapping/business?pretty=true
docker exec -it prv-elassandra curl -XPUT -H 'Content-Type: application/json' -d "$review" http://127.0.0.1:9200/review-times/_mapping/review?pretty=true
