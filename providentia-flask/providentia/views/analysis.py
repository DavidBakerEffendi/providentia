import json
import logging

from flask import Blueprint, Response
from flask_cors import cross_origin

from providentia.repository.this import tbl_analysis
from providentia.models import model_encoder

bp = Blueprint('analysis', __name__, )


@bp.route("/", methods=['GET'])
@cross_origin()
def result_get():
    """Show all the posts, most recent first."""
    try:
        results = tbl_analysis.query_results()
    except Exception as e:
        logging.error(str(e))
        return Response({"message": "Unexpected error while querying database!"}, status=500)

    if results is None:
        return Response({"message": "Database empty."}, status=200)

    return Response(json.dumps(results, default=model_encoder), status=200, mimetype='application/json')
