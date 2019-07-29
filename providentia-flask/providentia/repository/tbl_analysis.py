from flask import current_app

from providentia.db.this import get_db
from providentia.models import analysis_decoder

TABLE = 'analysis'
COLUMNS = ("id", "dataset_id", "name", "description")


def query_results(n=None):
    with current_app.app_context():
        cur = get_db().cursor()
        query = "SELECT id, dataset_id, name, description " \
                "FROM {} ORDER BY name DESC".format(TABLE)

        if n is None:
            cur.execute(query)
        else:
            cur.execute(query + " LIMIT %s", (str(n),))

        rows = []
        if cur.rowcount > 0:
            for row in cur.fetchall():
                rows.append(dict(zip(COLUMNS, row)))
        else:
            return None

        return [analysis_decoder(row) for row in rows]


def find(row_id):
    with current_app.app_context():
        cur = get_db().cursor()
        query = "SELECT id, dataset_id, name, description FROM {} WHERE id = %s".format(TABLE)

        cur.execute(query, (row_id,))

        if cur.rowcount > 0:
            result = dict(zip(COLUMNS, cur.fetchone()))
        else:
            return None

        return analysis_decoder(result)


def find_name(row_name):
    with current_app.app_context():
        cur = get_db().cursor()
        query = "SELECT id, dataset_id, name, description FROM {} WHERE name = %s".format(TABLE)

        cur.execute(query, (row_name,))

        if cur.rowcount > 0:
            result = dict(zip(COLUMNS, cur.fetchone()))
        else:
            return None

        return analysis_decoder(result)
