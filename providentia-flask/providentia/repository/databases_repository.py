import providentia.db
import providentia.entities.database
import logging
from flask import current_app
from config import default_config

config = default_config()
logging.basicConfig(level=config.LOGGING_LEVEL)


def query_results(n=None):
    with current_app.app_context():
        cur = providentia.db.get_db().cursor()
        query = "SELECT id, name, description, icon " \
                "FROM databases ORDER BY name DESC"

        if n is None:
            logging.debug("Executing query: %s", query)
            cur.execute(query)
        else:
            logging.debug("Executing query: %s LIMIT %d", query, n)
            cur.execute(query + " LIMIT %s", (str(n), ))

        columns = ("id", "name", "description", "icon")
        rows = []
        if cur.rowcount > 0:
            for row in cur.fetchall():
                rows.append(dict(zip(columns, row)))
        else:
            return None

        deserialized = []

        for row in rows:
            deserialized.append(providentia.entities.database.deserialize(row))

        return deserialized


def find(row_id):
    with current_app.app_context():
        cur = providentia.db.get_db().cursor()
        query = "SELECT id, name, description, icon " \
                "FROM databases WHERE id = %s"

        logging.debug("Executing query: SELECT id, name, description, icon FROM datasets WHERE id = %s", row_id)
        cur.execute(query, (row_id, ))

        columns = ("id", "name", "description", "icon")

        if cur.rowcount > 0:
            result = dict(zip(columns, cur.fetchone()))
        else:
            return None

        deserialized = providentia.entities.database.deserialize(result)

        return deserialized
