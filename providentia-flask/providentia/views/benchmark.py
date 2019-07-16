import json
import logging

from flask import Blueprint, Response, jsonify
from flask_cors import cross_origin

from providentia.repository.this import tbl_benchmark

bp = Blueprint('benchmark', __name__, )


@bp.route("/", methods=['GET'])
@cross_origin()
def result_get():
    """Show all the posts, most recent first."""
    try:
        results = tbl_benchmark.query_results(10)
    except Exception as e:
        logging.error("Error on GET /api/benchmark/: {}".format(str(e)))
        return jsonify(error="Unexpected error while querying database! The database is most likely down."), 500

    if results is None:
        return jsonify(error="No results in database, you can add more by navigating to 'New Job' in the sidebar."), 503

    # Serialize objects
    results = [result.__dict__ for result in results]

    return Response(json.dumps(results, default=str), status=200, mimetype='application/json')


@bp.route("/<benchmark_id>", methods=['GET'])
@cross_origin()
def result_find(benchmark_id):
    """Show all the posts, most recent first."""
    try:
        benchmark = tbl_benchmark.find(benchmark_id)
    except Exception as e:
        logging.error(str(e))
        return Response({"message": "Unexpected error while querying database!"}, status=500)

    if benchmark is None:
        return Response({"message": "Benchmark not found!"}, status=404)

    return Response(json.dumps(benchmark.json, default=str), status=200, mimetype='application/json')
