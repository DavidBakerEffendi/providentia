import json
import logging

from flask import Blueprint, Response, jsonify
from flask_cors import cross_origin

from providentia.repository import tbl_review_trends
from providentia.models import model_encoder

bp = Blueprint('result/review-trends', __name__, )


@bp.route("/<benchmark_id>", methods=['GET'])
@cross_origin()
def result_get(benchmark_id):
    try:
        results = tbl_review_trends.get_results(benchmark_id)
    except Exception as e:
        logging.error("Error on GET /api/result/review-trends: {}".format(str(e)))
        return jsonify(error="Unexpected error while querying database! The database is most likely down."), 500

    return Response(json.dumps(results, default=model_encoder), status=200, mimetype='application/json')
