-- CREATE DATABASE providentia;

CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

DROP TABLE IF EXISTS public.graphs;
DROP TABLE IF EXISTS public.results;

CREATE TABLE public.results (
    id uuid DEFAULT uuid_generate_v4(),
    database character varying(255) NOT NULL,
    dataset character varying(255) NOT NULL,
    date_executed timestamp without time zone NOT NULL,
    title character varying(255) NOT NULL,
    description character varying(255),
    query_time integer NOT NULL,
    analysis_time integer NOT NULL,
    PRIMARY KEY (id)
);

CREATE TABLE public.graphs (
    id uuid DEFAULT uuid_generate_v4(),
    result_id uuid NOT NULL,
    graphSON text NOT NULL,
    PRIMARY KEY (id),
    FOREIGN KEY (result_id) REFERENCES results (id)
);

