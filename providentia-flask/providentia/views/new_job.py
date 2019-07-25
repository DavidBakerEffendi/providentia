import logging

from flask import Blueprint, jsonify, request
from flask_cors import cross_origin

from providentia.models import new_benchmark_decoder
from providentia.repository import tbl_benchmark

bp = Blueprint('new-job', __name__, )


@bp.route("/", methods=['POST'])
@cross_origin()
def new_job():
    """Capture new job to begin processing."""
    data = request.get_json()
    logging.debug(data)

    try:
        benchmark = new_benchmark_decoder(data)
    except KeyError as e:
        logging.debug(str(e))
        return jsonify(error=str(e)), 404

    tbl_benchmark.insert(benchmark)

    return jsonify(message='Not yet implemented.'), 200
