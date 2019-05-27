-- CREATE DATABASE providentia;

CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

DROP TABLE IF EXISTS graphs;
DROP TABLE IF EXISTS results;
DROP TABLE IF EXISTS datasets;
DROP TABLE IF EXISTS databases;

CREATE TABLE databases (
    id uuid DEFAULT uuid_generate_v4(),
    name character varying(255) NOT NULL,
    description text NOT NULL,
    icon text NOT NULL,
    PRIMARY KEY (id)
);

CREATE TABLE datasets (
    id uuid DEFAULT uuid_generate_v4(),
    name character varying(255) NOT NULL,
    description text NOT NULL,
    icon text NOT NULL,
    PRIMARY KEY (id)
);

CREATE TABLE results (
    id uuid DEFAULT uuid_generate_v4(),
    database_id uuid NOT NULL,
    dataset_id uuid NOT NULL,
    date_executed timestamp without time zone NOT NULL,
    title character varying(255) NOT NULL,
    description text,
    query_time integer NOT NULL,
    analysis_time integer NOT NULL,
    PRIMARY KEY (id),
    FOREIGN KEY (database_id) REFERENCES databases (id),
    FOREIGN KEY (dataset_id) REFERENCES datasets (id)
);

CREATE TABLE graphs (
    id uuid DEFAULT uuid_generate_v4(),
    result_id uuid NOT NULL,
    graphson text NOT NULL,
    PRIMARY KEY (id),
    FOREIGN KEY (result_id) REFERENCES results (id)
);
