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


def get_performance(analysis_id):
    with current_app.app_context():
        cur = get_db().cursor()
        query = 'SELECT databases.name, AVG(query_time) as avg, STDDEV_SAMP(query_time) as stddev ' \
                'FROM benchmarks ' \
                'JOIN databases ON database_id = databases.id ' \
                'WHERE benchmarks.analysis_id = %s ' \
                'GROUP BY databases.name ORDER BY databases.name'
        cur.execute(query, (analysis_id,))

        perf_headings = ['name', 'avg', 'stddev']

        rows = []
        if cur.rowcount > 0:
            for row in cur.fetchall():
                rows.append(dict(zip(perf_headings, row)))
        else:
            return "No analysis results!"

        return rows
