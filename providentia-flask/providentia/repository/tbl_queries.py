from flask import current_app

from providentia.db.this import get_db
from providentia.models import query_decoder

TABLE = 'queries'
COLUMNS = ("id", "analysis_id", "database_id", "query", "language")


def get_queries(analysis_id, database_id):
    with current_app.app_context():
        cur = get_db().cursor()
        query = "SELECT id, analysis_id, database_id, query, language " \
                "FROM {} WHERE analysis_id = %s::uuid AND database_id = %s::uuid".format(TABLE)

        cur.execute(query, (analysis_id, database_id))

        rows = []
        if cur.rowcount > 0:
            for row in cur.fetchall():
                rows.append(dict(zip(COLUMNS, row)))
        else:
            return None

        return [query_decoder(row) for row in rows]
