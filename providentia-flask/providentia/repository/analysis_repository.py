import providentia.db
import providentia.entities.analysis
import logging
from flask import current_app
from config import default_config

config = default_config()
logging.basicConfig(level=config.LOGGING_LEVEL)

TABLE = 'analysis'
COLUMNS = ("id", "dataset_id", "name", "description")


def query_results(n=None):
    with current_app.app_context():
        cur = providentia.db.get_db().cursor()
        query = "SELECT id, dataset_id, name, description " \
                "FROM {} ORDER BY name DESC".format(TABLE)

        if n is None:
            logging.debug("Executing query: %s", query)
            cur.execute(query)
        else:
            logging.debug("Executing query: %s LIMIT %d", query, n)
            cur.execute(query + " LIMIT %s", (str(n),))

        rows = []
        if cur.rowcount > 0:
            for row in cur.fetchall():
                rows.append(dict(zip(COLUMNS, row)))
        else:
            return None

        deserialized = []

        for row in rows:
            deserialized.append(providentia.entities.analysis.deserialize(row))

        return deserialized


def find(row_id):
    with current_app.app_context():
        cur = providentia.db.get_db().cursor()
        query = "SELECT id, dataset_id, name, description " \
                "FROM {} WHERE id = %s".format(TABLE)

        logging.debug("Executing query: SELECT id, dataset_id, name, description FROM %s WHERE id = %s", TABLE, row_id)
        cur.execute(query, (row_id,))

        if cur.rowcount > 0:
            result = dict(zip(COLUMNS, cur.fetchone()))
        else:
            return None

        deserialized = providentia.entities.analysis.deserialize(result)

        return deserialized


def find_name(row_name):
    with current_app.app_context():
        cur = providentia.db.get_db().cursor()
        query = "SELECT id, name, description, icon FROM {} WHERE name = {}".format(TABLE, '%s')

        logging.debug("Executing query: SELECT id, name, description, icon FROM results WHERE name = %s", row_name)
        cur.execute(query, (row_name,))

        if cur.rowcount > 0:
            result = dict(zip(COLUMNS, cur.fetchone()))
        else:
            return None

        logging.debug(str(result))
        deserialized = providentia.entities.analysis.deserialize(result)

        return deserialized
