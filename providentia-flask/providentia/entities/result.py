from flask import Blueprint
from flask_cors import cross_origin
from providentia.db import get_db
import json
from flask import Response

bp = Blueprint('result', __name__, )


@bp.route("/", methods=['GET'])
@cross_origin()
def result_get():
    """Show all the posts, most recent first."""
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


@bp.route("/<result_id>", methods=['GET'])
@cross_origin()
def result_find(result_id):
    """Show all the posts, most recent first."""
    # TODO: Create response to handle DB error
    db = get_db()
    cur = db.cursor()

    try:
        cur.execute("SELECT id, database, date_executed, title, description, query_time, analysis_time "
                    "FROM results WHERE id = %s", [result_id])
    except Exception:
        return Response({"message": "Unexpected error while querying database!"}, status=500)

    columns = ("id", "database", "date_executed", "title", "description", "query_time", "analysis_time")
    if cur.rowcount > 0:
        result = dict(zip(columns, cur.fetchone()))
    else:
        return Response({"message": "Benchmark not found!"}, status=404)

    return Response(str(json.dumps(result, default=str)), status=200, mimetype='application/json')
