import logging

from flask import current_app
from gremlin_python.driver import client


def test_connection(app):
    gremlin_client = None
    with app.app_context():
        conn_string = current_app.config['JANUSGRAPH_YELP_CONN']

    try:
        gremlin_client = client.Client(conn_string, 'g')
        gremlin_client.submit("1 + 1").next()
    except Exception as e:
        logging.warn('Could not connect to JanusGraph! JanusGraph will not be available for benchmarking. %s',
                     str(e))
        return False
    finally:
        if gremlin_client is not None:
            gremlin_client.close()
    return True


def execute_query(query):
    gremlin_client = None
    with current_app.app_context():
        conn_string = current_app.config['JANUSGRAPH_YELP_CONN']

    try:
        gremlin_client = client.Client(conn_string, 'g')
        return gremlin_client.submit(query).next()
    except Exception as e:
        logging.warn('Error while executing query! %s', str(e))
        return str(e)
    finally:
        if gremlin_client is not None:
            gremlin_client.close()
