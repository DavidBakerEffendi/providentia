import providentia.db
import providentia.entities.dataset
import logging
from flask import current_app
from config import default_config

config = default_config()
logging.basicConfig(level=config.LOGGING_LEVEL)


def find(row_id):
    with current_app.app_context():
        cur = providentia.db.get_db().cursor()
        query = "SELECT id, name, description, icon " \
                "FROM datasets WHERE id = %s"

        # logging.debug("Executing query: %s", query, row_id)
        cur.execute(query, (row_id,))

        columns = ("id", "name", "description", "icon")

        if cur.rowcount > 0:
            result = dict(zip(columns, cur.fetchone()))
        else:
            return None

        deserialized = providentia.entities.dataset.deserialize(result)

        return deserialized
