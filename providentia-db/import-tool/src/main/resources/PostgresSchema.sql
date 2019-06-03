-- Create the database and connect to it after which run the uncommented queries to create
-- the schema and indexes for the Yelp database.

-- CREATE DATABASE yelp;

CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

DROP TABLE IF EXISTS review;
DROP TABLE IF EXISTS bus_cat;
DROP TABLE IF EXISTS business;
DROP TABLE IF EXISTS friends;
DROP TABLE IF EXISTS user;
DROP TABLE IF EXISTS category;
DROP TABLE IF EXISTS city;
DROP TABLE IF EXISTS state;

CREATE TABLE state (
    id uuid DEFAULT uuid_generate_v4(),
    name character varying(255) NOT NULL,
    PRIMARY KEY (id)
);

CREATE TABLE city (
    id uuid DEFAULT uuid_generate_v4(),
    name character varying(255) NOT NULL,
    state_id uuid NOT NULL,
    PRIMARY KEY (id),
    FOREIGN KEY (state_id) REFERENCES state (id)
);

CREATE TABLE category (
    id uuid DEFAULT uuid_generate_v4(),
    name character varying(255) NOT NULL,
    PRIMARY KEY (id)
);

CREATE TABLE users (
    id uuid DEFAULT uuid_generate_v4(),
    name character varying(255) NOT NULL,
    review_count integer DEFAULT 0,
    yelping_since timestamp NOT NULL,
    useful integer DEFAULT 0,
    funny integer DEFAULT 0,
    cool integer DEFAULT 0,
    fans integer DEFAULT 0,
    average_stars numeric DEFAULT 0.0,
    compliment_hot integer DEFAULT 0,
    compliment_more integer DEFAULT 0,
    compliment_profile integer DEFAULT 0,
    compliment_cute integer DEFAULT 0,
    compliment_list integer DEFAULT 0,
    compliment_note integer DEFAULT 0,
    compliment_plain integer DEFAULT 0,
    compliment_cool integer DEFAULT 0,
    compliment_funny integer DEFAULT 0,
    compliment_writer integer DEFAULT 0,
    compliment_photos integer DEFAULT 0,
    PRIMARY KEY (id)
);

CREATE TABLE friends (
    user_id uuid NOT NULL,
    friend_id uuid NOT NULL,
    FOREIGN KEY (user_id) REFERENCES users (id),
    FOREIGN KEY (friend_id) REFERENCES users (id)
);

CREATE TABLE business (
    id uuid DEFAULT uuid_generate_v4(),
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
    business_id uuid NOT NULL,
    category_id uuid NOT NULL,
    FOREIGN KEY (business_id) REFERENCES business (id),
    FOREIGN KEY (category_id) REFERENCES category (id)
);

CREATE TABLE review (
    id uuid DEFAULT uuid_generate_v4(),
    user_id uuid NOT NULL,
    business_id uuid NOT NULL,
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
