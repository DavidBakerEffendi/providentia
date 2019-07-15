import json
import logging

from flask import Blueprint, Response
from flask_cors import cross_origin

from providentia.repository.this import tbl_databases

bp = Blueprint('database', __name__, )


@bp.route("/", methods=['GET'])
@cross_origin()
def result_get():
    """Show all the posts, most recent first."""
    try:
        results = tbl_databases.query_results()
    except Exception as e:
        logging.error(str(e))
        return Response({"message": "Unexpected error while querying database!"}, status=500)

    if results is None:
        return Response({"message": "Database empty."}, status=200)

    # Serialize objects
    for i in range(len(results)):
        results[i] = results[i].json

    return Response(json.dumps(results, default=str), status=200, mimetype='application/json')
