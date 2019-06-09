-- CREATE DATABASE providentia;

CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

DROP TABLE IF EXISTS graphs;
DROP TABLE IF EXISTS benchmarks;
DROP TABLE IF EXISTS analysis;
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

CREATE TABLE analysis (
    id uuid DEFAULT uuid_generate_v4(),
    dataset_id uuid NOT NULL,
    name character varying(255) NOT NULL,
    description text NOT NULL,
    PRIMARY KEY (id),
    FOREIGN KEY (dataset_id) REFERENCES datasets (id)
);

CREATE TABLE benchmarks (
    id uuid DEFAULT uuid_generate_v4(),
    database_id uuid NOT NULL,
    dataset_id uuid NOT NULL,
    analysis_id uuid NOT NULL,
    date_executed timestamp without time zone,
    title character varying(255) NOT NULL,
    description text,
    query_time integer,
    analysis_time integer,
    status character varying(20) NOT NULL,   -- COMPLETE, WAITING, PROCESSING, UNCONFIRMED
    PRIMARY KEY (id),
    FOREIGN KEY (database_id) REFERENCES databases (id),
    FOREIGN KEY (dataset_id) REFERENCES datasets (id),
    FOREIGN KEY (analysis_id) REFERENCES analysis (id)
);

CREATE TABLE graphs (
    id uuid DEFAULT uuid_generate_v4(),
    result_id uuid NOT NULL,
    graphson text NOT NULL,
    PRIMARY KEY (id),
    FOREIGN KEY (result_id) REFERENCES benchmarks (id)
);
