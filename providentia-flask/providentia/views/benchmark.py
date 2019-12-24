import json
import logging

from flask import Blueprint, Response, jsonify
from flask_cors import cross_origin

from providentia.repository import tbl_benchmark
from providentia.models import model_encoder

bp = Blueprint('benchmark', __name__, )


@bp.route("/<no_results>", methods=['GET'])
@cross_origin()
def result_get(no_results=None):
    """Show all the posts, most recent first."""
    try:
        no_results = int(no_results)
    except Exception:
        no_results = 10

    try:
        if no_results < 0:
            results = tbl_benchmark.query_results()
        else:
            results = tbl_benchmark.query_results(no_results)
    except Exception as e:
        logging.error("Error on GET /api/benchmark/: {}".format(str(e)))
        return jsonify(error="Unexpected error while querying database! The database is most likely down."), 500

    if results is None:
        return jsonify(error="No results in database, you can add more by navigating to 'New Job' in the sidebar."), 503

    # Serialize objects
    results = [result.__dict__ for result in results]

    return Response(json.dumps(results, default=model_encoder), status=200, mimetype='application/json')


@bp.route("/find/<benchmark_id>", methods=['GET'])
@cross_origin()
def result_find(benchmark_id):
    """Get a specific benchmark."""
    try:
        benchmark = tbl_benchmark.find(benchmark_id)
    except Exception as e:
        logging.error(str(e))
        return Response({"Unexpected error while querying database!"}, status=500)

    if benchmark is None:
        return Response({"Benchmark not found!"}, status=404)

    return Response(json.dumps(benchmark, default=model_encoder), status=200, mimetype='application/json')


@bp.route("/total-benchmarks", methods=['GET'])
@cross_origin()
def total_benchmarks():
    """Get a total number of benchmarks."""
    try:
        total = tbl_benchmark.total()
        total = total[0] if total is not None else None
        if total is None:
            raise Exception
    except Exception as e:
        logging.error(str(e))
        return Response({"Unexpected error while querying database!"}, status=500)

    return Response(json.dumps({'total': total}, default=model_encoder), status=200, mimetype='application/json')


@bp.route("/paginate/page-size-<page_size>/<page_number>", methods=['GET'])
@cross_origin()
def paginate(page_size, page_number):
    """Get benchmarks limited to a page size and offset."""
    try:
        benchmarks = tbl_benchmark.paginate(page_size, page_number)
        if benchmarks is None:
            raise Exception
    except Exception as e:
        logging.error(str(e))
        return Response({"Unexpected error while querying database!"}, status=500)

    # Serialize objects
    results = [result.__dict__ for result in benchmarks]

    return Response(json.dumps(results, default=model_encoder), status=200, mimetype='application/json')
