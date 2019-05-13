--
-- PostgreSQL database dump
--

-- Dumped from database version 10.8 (Ubuntu 10.8-1.pgdg18.04+1)
-- Dumped by pg_dump version 10.8 (Ubuntu 10.8-1.pgdg18.04+1)

SET statement_timeout = 0;
SET lock_timeout = 0;
SET idle_in_transaction_session_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SELECT pg_catalog.set_config('search_path', '', false);
SET check_function_bodies = false;
SET xmloption = content;
SET client_min_messages = warning;
SET row_security = off;

--
-- Name: plpgsql; Type: EXTENSION; Schema: -; Owner: 
--

CREATE EXTENSION IF NOT EXISTS plpgsql WITH SCHEMA pg_catalog;


--
-- Name: EXTENSION plpgsql; Type: COMMENT; Schema: -; Owner: 
--

COMMENT ON EXTENSION plpgsql IS 'PL/pgSQL procedural language';


SET default_tablespace = '';

SET default_with_oids = false;

--
-- Name: databasechangelog; Type: TABLE; Schema: public; Owner: providentia
--

CREATE TABLE public.databasechangelog (
    id character varying(255) NOT NULL,
    author character varying(255) NOT NULL,
    filename character varying(255) NOT NULL,
    dateexecuted timestamp without time zone NOT NULL,
    orderexecuted integer NOT NULL,
    exectype character varying(10) NOT NULL,
    md5sum character varying(35),
    description character varying(255),
    comments character varying(255),
    tag character varying(255),
    liquibase character varying(20),
    contexts character varying(255),
    labels character varying(255),
    deployment_id character varying(10)
);


ALTER TABLE public.databasechangelog OWNER TO providentia;

--
-- Name: databasechangeloglock; Type: TABLE; Schema: public; Owner: providentia
--

CREATE TABLE public.databasechangeloglock (
    id integer NOT NULL,
    locked boolean NOT NULL,
    lockgranted timestamp without time zone,
    lockedby character varying(255)
);


ALTER TABLE public.databasechangeloglock OWNER TO providentia;

--
-- Name: prv_persistent_audit_event; Type: TABLE; Schema: public; Owner: providentia
--

CREATE TABLE public.prv_persistent_audit_event (
    event_id bigint NOT NULL,
    principal character varying(50) NOT NULL,
    event_date timestamp without time zone,
    event_type character varying(255)
);


ALTER TABLE public.prv_persistent_audit_event OWNER TO providentia;

--
-- Name: prv_persistent_audit_evt_data; Type: TABLE; Schema: public; Owner: providentia
--

CREATE TABLE public.prv_persistent_audit_evt_data (
    event_id bigint NOT NULL,
    name character varying(150) NOT NULL,
    value character varying(255)
);


ALTER TABLE public.prv_persistent_audit_evt_data OWNER TO providentia;

--
-- Name: sequence_generator; Type: SEQUENCE; Schema: public; Owner: providentia
--

CREATE SEQUENCE public.sequence_generator
    START WITH 1050
    INCREMENT BY 50
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.sequence_generator OWNER TO providentia;

--
-- Name: databasechangeloglock databasechangeloglock_pkey; Type: CONSTRAINT; Schema: public; Owner: providentia
--

ALTER TABLE ONLY public.databasechangeloglock
    ADD CONSTRAINT databasechangeloglock_pkey PRIMARY KEY (id);


--
-- Name: prv_persistent_audit_event prv_persistent_audit_event_pkey; Type: CONSTRAINT; Schema: public; Owner: providentia
--

ALTER TABLE ONLY public.prv_persistent_audit_event
    ADD CONSTRAINT prv_persistent_audit_event_pkey PRIMARY KEY (event_id);


--
-- Name: prv_persistent_audit_evt_data prv_persistent_audit_evt_data_pkey; Type: CONSTRAINT; Schema: public; Owner: providentia
--

ALTER TABLE ONLY public.prv_persistent_audit_evt_data
    ADD CONSTRAINT prv_persistent_audit_evt_data_pkey PRIMARY KEY (event_id, name);


--
-- Name: idx_persistent_audit_event; Type: INDEX; Schema: public; Owner: providentia
--

CREATE INDEX idx_persistent_audit_event ON public.prv_persistent_audit_event USING btree (principal, event_date);


--
-- Name: idx_persistent_audit_evt_data; Type: INDEX; Schema: public; Owner: providentia
--

CREATE INDEX idx_persistent_audit_evt_data ON public.prv_persistent_audit_evt_data USING btree (event_id);


--
-- Name: prv_persistent_audit_evt_data fk_evt_pers_audit_evt_data; Type: FK CONSTRAINT; Schema: public; Owner: providentia
--

ALTER TABLE ONLY public.prv_persistent_audit_evt_data
    ADD CONSTRAINT fk_evt_pers_audit_evt_data FOREIGN KEY (event_id) REFERENCES public.prv_persistent_audit_event(event_id);


--
-- PostgreSQL database dump complete
--
