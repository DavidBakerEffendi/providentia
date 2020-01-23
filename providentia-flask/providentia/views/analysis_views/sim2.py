import json
import logging

from flask import Blueprint, Response, jsonify
from flask_cors import cross_origin

from providentia.repository import tbl_sim2
from providentia.models import model_encoder

bp = Blueprint('result/sim2', __name__, )


@bp.route("/<sim_id>", methods=['GET'])
@cross_origin()
def result_get(sim_id):
    try:
        results = tbl_sim2.get_results(sim_id)
    except Exception as e:
        logging.error("Error on GET /api/result/sim2: {}".format(str(e)))
        return jsonify(error="Unexpected error while querying database! The database is most likely down."), 500

    return Response(json.dumps(results, default=model_encoder), status=200, mimetype='application/json')
