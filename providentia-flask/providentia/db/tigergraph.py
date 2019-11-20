import logging

import requests
from flask import current_app
from requests.exceptions import HTTPError


def test_connection(app):
    with app.app_context():
        conn_string = current_app.config['TIGERGRAPH_YELP_CONN']

    try:
        response = requests.get(conn_string + 'version')
        response.raise_for_status()
        return True
    except Exception as e:
        logging.warning('Could not connect to TigerGraph! TigerGraph will not be available for benchmarking. %s', e)
        return False


def execute_query(query):
    with current_app.app_context():
        conn_string = "{}{}{}".format(current_app.config['TIGERGRAPH_YELP_CONN'], 'query/MyGraph/', query)

    try:
        response = requests.get(conn_string)
        response.raise_for_status()
        if response.json() is not None:
            return response.json()['results']
        else:
            return None
    except HTTPError as http_err:
        logging.warn('Could not query TigerGraph! Error: %s', http_err)
        return False
    except Exception as e:
        logging.warn('Python exception encountered while attempting to query TigerGraph! Error: %s', e)
