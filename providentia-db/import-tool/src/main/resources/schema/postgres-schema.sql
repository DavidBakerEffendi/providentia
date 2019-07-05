-- From the psql bash, run the following to create the database and schema:

-- To create and connect to the database, run from here

CREATE DATABASE yelp;
\c yelp;

-- Use PostGis

CREATE EXTENSION postgis;

-- If already connected to the database, run from here to create the schema

DROP TABLE IF EXISTS review;
DROP TABLE IF EXISTS bus_by_cat;
DROP TABLE IF EXISTS business;
DROP TABLE IF EXISTS friends;
DROP TABLE IF EXISTS users;
DROP TABLE IF EXISTS city;

DROP INDEX IF EXISTS review_date_ind;
DROP INDEX IF EXISTS business_location_ind;
DROP INDEX IF EXISTS business_city_ind;

CREATE TABLE city (
    id CHARACTER VARYING(50) UNIQUE,
    state CHARACTER VARYING(2) NOT NULL,
    PRIMARY KEY (id, state)
);

CREATE TABLE users (
    id CHARACTER VARYING(22) UNIQUE,
    name CHARACTER VARYING(255) NOT NULL,
    review_count INTEGER DEFAULT 0,
    yelping_since TIMESTAMP NOT NULL,
    useful INTEGER DEFAULT 0,
    funny INTEGER DEFAULT 0,
    cool INTEGER DEFAULT 0,
    fans INTEGER DEFAULT 0,
    average_stars NUMERIC DEFAULT 0.0,
    PRIMARY KEY (id)
);

CREATE TABLE friends (
    user_id CHARACTER VARYING(22),
    friend_id CHARACTER VARYING(22) NOT NULL,
    PRIMARY KEY (user_id, friend_id),
    FOREIGN KEY (user_id) REFERENCES users (id),
    FOREIGN KEY (friend_id) REFERENCES users (id)
);

CREATE TABLE business (
    id CHARACTER VARYING(22) UNIQUE,
    name CHARACTER VARYING(255) NOT NULL,
    address CHARACTER VARYING(255) NOT NULL,
    city CHARACTER VARYING(50) NOT NULL,
    postal_code CHARACTER VARYING(50) NOT NULL,
    location geometry(Point, 4326) NOT NULL,
    stars NUMERIC NOT NULL,
    review_count INTEGER DEFAULT 0,
    is_open boolean DEFAULT false,
    PRIMARY KEY (id),
    FOREIGN KEY (city) REFERENCES city (id)
);

CREATE TABLE bus_by_cat (
    business_id CHARACTER VARYING(22) NOT NULL,
    category CHARACTER VARYING(50) NOT NULL,
    PRIMARY KEY (business_id, category),
    FOREIGN KEY (business_id) REFERENCES business (id)
);

CREATE TABLE review (
    id CHARACTER VARYING(22) UNIQUE,
    user_id CHARACTER VARYING(22) NOT NULL,
    business_id CHARACTER VARYING(22) NOT NULL,
    stars NUMERIC NOT NULL,
    useful INTEGER DEFAULT 0,
    funny INTEGER DEFAULT 0,
    cool INTEGER DEFAULT 0,
    text TEXT NOT NULL,
    date TIMESTAMP NOT NULL,
    PRIMARY KEY (id),
    FOREIGN KEY (user_id) REFERENCES users (id),
    FOREIGN KEY (business_id) REFERENCES business (id)
);
-- Index spatio-temporal data
CREATE INDEX review_date_ind ON review (date);
CREATE INDEX business_location_ind ON business USING GIST (location);
CREATE INDEX business_city_ind ON business (city);
-- Cluster reviews by date and businesses by location
CLUSTER review USING review_date_ind;
CLUSTER business USING business_city_ind;
