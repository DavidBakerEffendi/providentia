import json
import logging

from flask import Blueprint, Response, jsonify
from flask_cors import cross_origin

from providentia.repository import tbl_queries
from providentia.models import model_encoder

bp = Blueprint('queries', __name__, )


@bp.route("/<analysis_id>+<database_id>", methods=['GET'])
@cross_origin()
def result_get(analysis_id, database_id):
    try:
        results = tbl_queries.get_queries(analysis_id, database_id)
    except Exception as e:
        logging.error("Error on GET /api/queries/: {}".format(str(e)))
        return jsonify(error="Unexpected error while querying database! The database is most likely down."), 500

    return Response(json.dumps(results, default=model_encoder), status=200, mimetype='application/json')
