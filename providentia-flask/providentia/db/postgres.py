import logging

import psycopg2
from flask import current_app


def test_connection(app):
    sql_conn = None
    with app.app_context():
        conn_string = current_app.config['POSTGRES_YELP_CONN']

    try:
        sql_conn = psycopg2.connect(conn_string)
        sql_conn.cursor().execute('SELECT 1 + 1;')
    except Exception as e:
        logging.warn('Could not connect to PostgreSQL! PostgreSQL will not be available for benchmarking. %s',
                     str(e))
        return False
    finally:
        if sql_conn is not None:
            sql_conn.close()
    return True


def execute_query(query):
    sql_conn = None
    with current_app.app_context():
        conn_string = current_app.config['POSTGRES_YELP_CONN']

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
