-- From the psql bash, run the following to create the database and schema:

-- To create and connect to the database, run from here
CREATE DATABASE yelp;
\c yelp;

-- Use PostGis

CREATE EXTENSION postgis;

-- If already connected to the database, run from here to create the schema

DROP TABLE IF EXISTS review;
DROP TABLE IF EXISTS bus_2_cat;
DROP TABLE IF EXISTS business;
DROP TABLE IF EXISTS category;
DROP TABLE IF EXISTS friends;
DROP TABLE IF EXISTS users;
DROP TABLE IF EXISTS city;

DROP INDEX IF EXISTS review_date_ind;
DROP INDEX IF EXISTS business_location_ind;
DROP INDEX IF EXISTS business_city_ind;

CREATE TABLE city
(
    id    CHARACTER VARYING(50) UNIQUE,
    state CHARACTER VARYING(2) NOT NULL,
    PRIMARY KEY (id, state)
);

CREATE TABLE users
(
    id            CHARACTER VARYING(22) UNIQUE,
    name          CHARACTER VARYING(255) NOT NULL,
    yelping_since TIMESTAMP              NOT NULL,
    useful        INTEGER DEFAULT 0,
    funny         INTEGER DEFAULT 0,
    cool          INTEGER DEFAULT 0,
    fans          INTEGER DEFAULT 0,
    PRIMARY KEY (id)
);

-- Many-to-many table for user to user relationships
CREATE TABLE friends
(
    user_id   CHARACTER VARYING(22),
    friend_id CHARACTER VARYING(22) NOT NULL,
    FOREIGN KEY (user_id) REFERENCES users (id),
    FOREIGN KEY (friend_id) REFERENCES users (id)
);

CREATE TABLE business
(
    id          CHARACTER VARYING(22) UNIQUE,
    name        CHARACTER VARYING(255) NOT NULL,
    address     CHARACTER VARYING(255) NOT NULL,
    city        CHARACTER VARYING(50)  NOT NULL,
    postal_code CHARACTER VARYING(50)  NOT NULL,
    location    geometry(Point, 4326)  NOT NULL,
    stars       NUMERIC                NOT NULL,
    is_open     boolean DEFAULT false,
    PRIMARY KEY (id),
    FOREIGN KEY (city) REFERENCES city (id)
);

CREATE TABLE category
(
    id   SERIAL,
    name TEXT NOT NULL,
    PRIMARY KEY (id)
);

-- Many-to-many table for business to category relationships
CREATE TABLE bus_2_cat
(
    business_id CHARACTER VARYING(22) NOT NULL,
    category_id INTEGER               NOT NULL,
    FOREIGN KEY (business_id) REFERENCES business (id),
    FOREIGN KEY (category_id) REFERENCES category (id)
);

CREATE TABLE review
(
    id          CHARACTER VARYING(22) UNIQUE,
    user_id     CHARACTER VARYING(22) NOT NULL,
    business_id CHARACTER VARYING(22) NOT NULL,
    stars       NUMERIC               NOT NULL,
    useful      INTEGER DEFAULT 0,
    funny       INTEGER DEFAULT 0,
    cool        INTEGER DEFAULT 0,
    text        TEXT                  NOT NULL,
    date        TIMESTAMP             NOT NULL,
    PRIMARY KEY (id),
    FOREIGN KEY (user_id) REFERENCES users (id),
    FOREIGN KEY (business_id) REFERENCES business (id)
);

-- Index spatio-temporal data and category names
CREATE INDEX review_date_ind ON review (date);
CREATE INDEX business_location_ind ON business USING GIST (location);
CREATE INDEX business_city_ind ON business (city);
CREATE INDEX category_name_ind ON category (name);

-- Cluster reviews by date and businesses by location
CLUSTER review USING review_date_ind;
CLUSTER business USING business_city_ind;
