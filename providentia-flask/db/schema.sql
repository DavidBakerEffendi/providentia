CREATE DATABASE providentia;
\c providentia;

CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

DROP TABLE IF EXISTS graphs;
DROP TABLE IF EXISTS queries;
DROP TABLE IF EXISTS kate_analysis;
DROP TABLE IF EXISTS review_trend_analysis;
DROP TABLE IF EXISTS benchmarks;
DROP TABLE IF EXISTS analysis;
DROP TABLE IF EXISTS datasets;
DROP TABLE IF EXISTS databases;
DROP TABLE IF EXISTS cpu_logs;
DROP TABLE IF EXISTS server_logs;

CREATE TABLE databases (
    id uuid DEFAULT uuid_generate_v4(),
    name character varying(255) NOT NULL,
    description text NOT NULL,
    icon text NOT NULL,
    status text NOT NULL DEFAULT 'DOWN', -- UP, DOWN
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
    date_executed timestamp,
    query_time integer,
    analysis_time integer,
    status character varying(20) NOT NULL,   -- COMPLETE, WAITING, PROCESSING
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

CREATE TABLE queries (
    id uuid DEFAULT uuid_generate_v4(),
    analysis_id uuid NOT NULL,
    database_id uuid NOT NULL,
    query text NOT NULL,
    language text NOT NULL,
    PRIMARY KEY (id),
    FOREIGN KEY (analysis_id) REFERENCES analysis (id),
    FOREIGN KEY (database_id) REFERENCES databases (id)
);

CREATE TABLE kate_analysis (
    id uuid DEFAULT uuid_generate_v4(),
    benchmark_id uuid NOT NULL,
    business text NOT NULL,
    sentiment_average FLOAT NOT NULL,
    star_average FLOAT NOT NULL,
    total_reviews INTEGER NOT NULL,
    PRIMARY KEY (id),
    FOREIGN KEY (benchmark_id) REFERENCES benchmarks (id)
);

CREATE TABLE review_trend_analysis (
    id uuid DEFAULT uuid_generate_v4(),
    benchmark_id uuid NOT NULL,
    stars FLOAT NOT NULL,
    length FLOAT NOT NULL,
    cool FLOAT NOT NULL,
    funny FLOAT NOT NULL,
    useful FLOAT NOT NULL,
    sentiment FLOAT NOT NULL,
    PRIMARY KEY (id),
    FOREIGN KEY (benchmark_id) REFERENCES benchmarks (id)
);

CREATE TABLE server_logs (
    id SERIAL,
    captured_at timestamp,
    memory_perc float NOT NULL,
    PRIMARY KEY (id)
);

CREATE TABLE cpu_logs (
    id SERIAL,
    system_log_id int NOT NULL,
    core_id int NOT NULL DEFAULT 0,
    cpu_perc float NOT NULL,
    PRIMARY KEY (id),
    FOREIGN KEY (system_log_id) REFERENCES server_logs (id)
);
