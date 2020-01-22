import logging

import psycopg2
from flask import current_app


def test_connection(app):
    sql_conn = None
    success = False
    with app.app_context():
        yelp_conn_string = current_app.config['POSTGRES_YELP_CONN'] + 'yelp'
        sim_conn_string = current_app.config['POSTGRES_YELP_CONN'] + 'phosim'

    try:
        sql_conn = psycopg2.connect(yelp_conn_string)
        sql_conn.cursor().execute('SELECT 1 + 1;')
        success = True
    except Exception as e:
        logging.warn('Could not connect to Yelp database in PostgreSQL! Yelp will not be available for benchmarking. %s',
                     str(e))

    try:
        sql_conn = psycopg2.connect(sim_conn_string)
        sql_conn.cursor().execute('SELECT 1 + 1;')
        success = True
    except Exception as e:
        logging.warn('Could not connect to Simulation database in PostgreSQL! Simulation will not be available for benchmarking. %s',
                     str(e))

    finally:
        if sql_conn is not None:
            sql_conn.close()
    return success


def execute_query(query, db):
    sql_conn = None
    with current_app.app_context():
        conn_string = '{}{}'.format(current_app.config['POSTGRES_YELP_CONN'], db)

    try:
        sql_conn = psycopg2.connect(conn_string)
        cur = sql_conn.cursor()
        cur.execute(query)
        rows = cur.fetchall()
        return rows
    except Exception as e:
        logging.warn('Error while executing query! %s', str(e))
        return str(e)
    finally:
        if sql_conn is not None:
            sql_conn.close()
