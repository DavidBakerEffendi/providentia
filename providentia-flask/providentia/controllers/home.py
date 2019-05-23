from flask import Blueprint
from flask_cors import cross_origin
from providentia.db import get_db
import json
from flask import Response

bp = Blueprint('home', __name__, )


@bp.route("/metrics", methods=['GET'])
@cross_origin()
def result_get():
    """Show all the posts, most recent first."""

    # TODO: Obtain database metrics from this DB and Spring etc
    db = get_db()
    cur = db.cursor()
    try:
        cur.execute("SELECT id, database, date_executed, title, description, query_time, analysis_time "
                    "FROM results ORDER BY date_executed DESC LIMIT 10")
    except Exception:
        return Response({"message": "Unexpected error while querying database!"}, status=500)

    columns = ("id", "database", "date_executed", "title", "description", "query_time", "analysis_time")
    results = []
    if cur.rowcount > 0:
        for row in cur.fetchall():
            results.append(dict(zip(columns, row)))
    else:
        return Response({"message": "Database empty."}, status=200)

    return Response(str(json.dumps(results, default=str)), status=200, mimetype='application/json')

