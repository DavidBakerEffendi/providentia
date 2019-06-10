// Create the database and connect to it before running the following script:

// Strategy: While declaring strategy name in Cassandra. There are two kinds of strategies declared
// in Cassandra Syntax. DataStax adds a third, namely Graph Strategy, with their drivers.
// GraphStrategy is only used when Cassandra is used as a back end for a graph DBMS.

// Run from here to create and connect to keyspace

CREATE KEYSPACE yelp WITH replication = {'class': 'SimpleStrategy', 'replication_factor' : 1};
USE yelp;

// If already connected to the keyspace, run from here to create the schema

DROP INDEX IF EXISTS yelp.city;
DROP INDEX IF EXISTS yelp.state;

DROP TABLE IF EXISTS yelp.review;
DROP TABLE IF EXISTS yelp.busByCat;
DROP TABLE IF EXISTS yelp.business;
DROP TABLE IF EXISTS yelp.users;
DROP TABLE IF EXISTS yelp.category;

// Note: Cassandra has no concept of foreign keys or default values. If a value is null then the column for that entry
// simply does not exist. Due to this, our Cassandra schema will vary slightly with bookkeeping having to be done
// ourselves.

CREATE TABLE yelp.users (
    id TEXT,
    name TEXT,
    review_count INT,
    yelping_since TIMESTAMP,
    useful INT,
    funny INT,
    cool INT,
    fans INT,
    average_stars FLOAT,
    friends LIST<TEXT>,
--    compliment_hot INT,
--    compliment_more INT,
--    compliment_profile INT,
--    compliment_cute INT
--    compliment_list INT,
--    compliment_note INT,
--    compliment_plain INT,
--    compliment_cool INT,
--    compliment_funny INT,
--    compliment_writer INT,
--    compliment_photos INT,
    PRIMARY KEY (id, yelping_since)
    // Cluster by temporal data
) WITH CLUSTERING ORDER BY (yelping_since DESC);

CREATE TABLE yelp.business (
    id TEXT,
    name TEXT,
    address TEXT,
    city TEXT,
    state TEXT,
    postal_code TEXT,
    latitude FLOAT,
    longitude FLOAT,
    stars FLOAT,
    review_count INT,
    is_open BOOLEAN,
    // Partition id and cluster by spatial. To quickly get businesses in city and state then
    // query from other tables
    PRIMARY KEY (id, longitude, latitude)
);

CREATE TABLE yelp.busByCat (
    category TEXT,
    business_id TEXT,
    // Partition by category_id, cluster by business_id
    PRIMARY KEY (category, business_id)
);

CREATE TABLE yelp.review (
    id TEXT,
    user_id TEXT,
    business_id TEXT,
    stars FLOAT,
    useful INT,
    funny INT,
    cool INT,
    text TEXT,
    date TIMESTAMP,
    // Use the IDs together as a partition key and cluster by date
    PRIMARY KEY ((id, user_id, business_id), date)
) WITH CLUSTERING ORDER BY (date DESC); // cluster by temporal data

// In Cassandra, high-cardinality indexes are a bad idea. We have the option of ALLOW FILTERING for range queries
// but we may not get a decent performance gain. Range queries are expensive in general so we need a workaround to
// limit the search area by named locations for example.

// Since Cassandra does not support joins, we will index the low-cardinality columns instead.

CREATE INDEX city ON yelp.business (city);
CREATE INDEX state ON yelp.business (state);
