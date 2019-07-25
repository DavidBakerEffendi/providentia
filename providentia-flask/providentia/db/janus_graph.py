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
        # this method blocks until the request is written to the server
        result_set = gremlin_client.submit(query)
        # the all method returns a concurrent.futures.Future
        future_results = result_set.all()
        # block until the script is evaluated and results are sent back by the server
        return future_results.result()
    except Exception as e:
        logging.warn('Error while executing query! %s', str(e))
        return str(e)
    finally:
        if gremlin_client is not None:
            gremlin_client.close()
