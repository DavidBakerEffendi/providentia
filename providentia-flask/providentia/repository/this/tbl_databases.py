import logging

from flask import current_app

from providentia.db.this import get_db
from providentia.models import database_decoder

TABLE = 'databases'
COLUMNS = ("id", "name", "description", "icon", "status")


def query_results(n=None):
    with current_app.app_context():
        cur = get_db().cursor()
        query = "SELECT id, name, description, icon, status " \
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

        deserialized = [database_decoder(row) for row in rows]

        return deserialized


def find(row_id):
    with current_app.app_context():
        cur = get_db().cursor()
        query = "SELECT id, name, description, icon " \
                "FROM {} WHERE id = %s".format(TABLE)

        logging.debug("Executing query: %s", query.replace('%s', '{}').format(row_id))
        cur.execute(query, (row_id,))

        if cur.rowcount > 0:
            result = dict(zip(COLUMNS, cur.fetchone()))
        else:
            return None

        deserialized = database_decoder(result)

        return deserialized


def find_name(row_name):
    with current_app.app_context():
        cur = get_db().cursor()
        query = "SELECT id, name, description, icon FROM {} WHERE name = %s".format(TABLE)

        logging.debug("Executing query: %s", query.replace('%s', '{}').format(row_name))
        cur.execute(query, (row_name,))

        if cur.rowcount > 0:
            result = dict(zip(COLUMNS, cur.fetchone()))
        else:
            return None

        deserialized = database_decoder(result)

        return deserialized


def set_status(db_name, status, app):
    if status == 'DOWN' or status == 'UP':
        with app.app_context():
            conn = get_db()
            cur = get_db().cursor()
            query = "UPDATE {} SET status = %s WHERE name = %s".format(TABLE)
            logging.debug("Executing query: %s", query.replace('%s', '{}').format(status, db_name))
            cur.execute(query, (status, db_name,))
            if cur.rowcount > 0:
                conn.commit()
            else:
                return None
    else:
        raise AttributeError('Database status may only be UP or DOWN')
