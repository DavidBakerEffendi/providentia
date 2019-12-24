import logging

from flask import Blueprint, jsonify, request
from flask_cors import cross_origin

from providentia.models import new_benchmark_decoder
from providentia.repository import tbl_benchmark

bp = Blueprint('new-job', __name__, )


@bp.route("/num-jobs/<num_jobs>", methods=['POST'])
@cross_origin()
def new_job(num_jobs):
    """Capture new job to begin processing."""
    data = request.get_json()
    logging.debug(data)

    try:
        benchmark = new_benchmark_decoder(data)
        num_jobs = int(num_jobs)
    except KeyError as e:
        logging.debug(str(e))
        return jsonify(error=str(e)), 404

    for i in range(num_jobs):
        tbl_benchmark.insert(benchmark)

    return jsonify(message='Not yet implemented.'), 200
