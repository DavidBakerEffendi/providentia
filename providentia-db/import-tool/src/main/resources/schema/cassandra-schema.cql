// Create the database and connect to it before running the following script:

// Strategy: While declaring strategy name in Cassandra. There are two kinds of strategies declared
// in Cassandra Syntax. DataStax adds a third, namely Graph Strategy, with their drivers.
// GraphStrategy is only used when Cassandra is used as a back end for a graph DBMS.

// Run from here to create and connect to keyspace
DROP KEYSPACE IF EXISTS yelp;
CREATE KEYSPACE yelp WITH replication = { 'class': 'NetworkTopologyStrategy', 'DC1': 1 };
USE yelp;

// Create geo_point for ElasticSearch

CREATE TYPE IF NOT EXISTS yelp.geo_point (
  lat double,
  lon double
);

// Note: Cassandra has no concept of foreign keys or default values. If a value is null then the column for that entry
// simply does not exist. Due to this, our Cassandra schema will vary slightly with bookkeeping having to be done
// ourselves.

CREATE TABLE yelp.users (
    user_id TEXT,
    name TEXT,
    review_count INT,
    yelping_since TIMESTAMP,
    useful INT,
    funny INT,
    cool INT,
    fans INT,
    average_stars FLOAT,
    friends SET<TEXT>,
    PRIMARY KEY (user_id, yelping_since)
    // Cluster by temporal data
) WITH CLUSTERING ORDER BY (yelping_since DESC);

CREATE TABLE yelp.business (
    business_id TEXT,
    name TEXT,
    address TEXT,
    city TEXT,
    state TEXT,
    postal_code TEXT,
    location FROZEN<geo_point>,
    categories SET<TEXT>,
    stars FLOAT,
    review_count INT,
    is_open BOOLEAN,
    // Partition id and cluster by spatial. To quickly get businesses in city and state then
    // query from other tables
    PRIMARY KEY (business_id, location)
);

CREATE TABLE yelp.review (
    review_id TEXT,
    user_id TEXT,
    business_id TEXT,
    stars FLOAT,
    useful INT,
    funny INT,
    cool INT,
    text TEXT,
    date TIMESTAMP,
    // Use the IDs together as a partition key and cluster by date
    PRIMARY KEY (review_id, date)
) WITH CLUSTERING ORDER BY (date DESC); // cluster by temporal data

// In Cassandra, high-cardinality indexes are a bad idea. We have the option of ALLOW FILTERING for range queries
// but we may not get a decent performance gain. Range queries are expensive in general so we need a workaround to
// limit the search area by named locations for example.

// Since Cassandra does not support joins, we will index the low-cardinality columns instead.

CREATE INDEX city ON yelp.business (city);
CREATE INDEX state ON yelp.business (state);
