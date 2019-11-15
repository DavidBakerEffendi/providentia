import json
import logging

from flask import Blueprint, Response
from flask_cors import cross_origin

from providentia.repository import tbl_analysis
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
        return Response({"Unexpected error while querying database!"}, status=500)

    if results is None:
        return Response({"Database empty."}, status=200)

    return Response(json.dumps(results, default=model_encoder), status=200, mimetype='application/json')


@bp.route("/performance/<analysis_id>", methods=['GET'])
@cross_origin()
def result_find(analysis_id):
    """Get a specific benchmark."""
    try:
        analysis = tbl_analysis.get_performance(analysis_id)
    except Exception as e:
        logging.error(str(e))
        return Response({"Unexpected error while querying database!"}, status=500)

    if analysis is None:
        return Response({"Analysis not found!"}, status=404)
    elif analysis == "No analysis results!":
        return Response({"No analysis results found!"}, status=503)

    return Response(json.dumps(analysis, default=model_encoder), status=200, mimetype='application/json')
