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
    except HTTPError as http_err:
        logging.warn('Could not connect to TigerGraph! TigerGraph will not be available for benchmarking. %s', http_err)
        return False
