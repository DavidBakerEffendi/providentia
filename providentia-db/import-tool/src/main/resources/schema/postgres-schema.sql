-- From the psql bash, run the following to create the database and schema:

-- To create and connect to the database, run from here

CREATE DATABASE yelp;
\c yelp;

-- If already connected to the database, run from here to create the schema

CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

DROP TABLE IF EXISTS review;
DROP TABLE IF EXISTS bus_cat;
DROP TABLE IF EXISTS business;
DROP TABLE IF EXISTS friends;
DROP TABLE IF EXISTS users;
DROP TABLE IF EXISTS category;
DROP TABLE IF EXISTS city;
DROP TABLE IF EXISTS state;

DROP INDEX IF EXISTS review_date;
DROP INDEX IF EXISTS business_location;

CREATE TABLE state (
    id uuid DEFAULT uuid_generate_v4(),
    name character varying(255) NOT NULL,
    PRIMARY KEY (id, name)
);

CREATE TABLE city (
    id uuid DEFAULT uuid_generate_v4(),
    name character varying(255) NOT NULL,
    state_id uuid NOT NULL,
    PRIMARY KEY (id, name),
    FOREIGN KEY (state_id) REFERENCES state (id)
);

-- TODO: Change this to simply have the name by the many to many table
CREATE TABLE category (
    id uuid DEFAULT uuid_generate_v4(),
    name character varying(255) NOT NULL,
    PRIMARY KEY (id, name)
);

CREATE TABLE users (
    id character varying(22) NOT NULL,
    name character varying(255) NOT NULL,
    review_count integer DEFAULT 0,
    yelping_since timestamp NOT NULL,
    useful integer DEFAULT 0,
    funny integer DEFAULT 0,
    cool integer DEFAULT 0,
    fans integer DEFAULT 0,
    average_stars numeric DEFAULT 0.0,
--    compliment_hot integer DEFAULT 0,
--    compliment_more integer DEFAULT 0,
--    compliment_profile integer DEFAULT 0,
--    compliment_cute integer DEFAULT 0,
--    compliment_list integer DEFAULT 0,
--    compliment_note integer DEFAULT 0,
--    compliment_plain integer DEFAULT 0,
--    compliment_cool integer DEFAULT 0,
--    compliment_funny integer DEFAULT 0,
--    compliment_writer integer DEFAULT 0,
--    compliment_photos integer DEFAULT 0,
    PRIMARY KEY (id)
);

CREATE TABLE friends (
    user_id character varying(22),
    friend_id character varying(22) NOT NULL,
    PRIMARY KEY (user_id, friend_id),
    FOREIGN KEY (user_id) REFERENCES users (id),
    FOREIGN KEY (friend_id) REFERENCES users (id)
);

CREATE TABLE business (
    id character varying(22),
    name character varying(255) NOT NULL,
    address character varying(255) NOT NULL,
    city_id uuid NOT NULL,
    state_id uuid NOT NULL,
    postal_code character varying(50) NOT NULL,
    latitude numeric NOT NULL,
    longitude numeric NOT NULL,
    stars numeric NOT NULL,
    review_count integer DEFAULT 0,
    is_open boolean DEFAULT false,
    PRIMARY KEY (id),
    FOREIGN KEY (city_id) REFERENCES city (id),
    FOREIGN KEY (state_id) REFERENCES state (id)
);

CREATE TABLE bus_cat (
    business_id character varying(22) NOT NULL,
    category_id uuid NOT NULL,
    PRIMARY KEY (business_id, category_id),
    FOREIGN KEY (business_id) REFERENCES business (id),
    FOREIGN KEY (category_id) REFERENCES category (id)
);

CREATE TABLE review (
    id character varying(22),
    user_id character varying(22) NOT NULL,
    business_id character varying(22) NOT NULL,
    stars numeric NOT NULL,
    useful integer DEFAULT 0,
    funny integer DEFAULT 0,
    cool integer DEFAULT 0,
    text text NOT NULL,
    date timestamp NOT NULL,
    PRIMARY KEY (id),
    FOREIGN KEY (user_id) REFERENCES users (id),
    FOREIGN KEY (business_id) REFERENCES business (id)
);

CREATE INDEX review_date ON review (date);
CREATE INDEX business_location on business (latitude, longitude);
CREATE INDEX category_name ON category (name);
