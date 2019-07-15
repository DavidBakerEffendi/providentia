import logging

from flask import Blueprint, jsonify, request
from flask_cors import cross_origin

from providentia.models import Benchmark
from providentia.repository.this import tbl_benchmark

bp = Blueprint('new-job', __name__, )


#######################################################################################################################
# REST Controllers
#######################################################################################################################


@bp.route("/", methods=['POST'])
@cross_origin()
def new_job():
    """Capture new job to begin processing."""
    data = request.get_json()
    logging.debug(data)

    # Check for uniqueness
    if tbl_benchmark.find_title(data['title']) is not None:
        logging.debug("Job title not unique!")
        return jsonify(error='Job title not unique!'), 400

    try:
        benchmark = deserialize(data)
    except KeyError as e:
        logging.debug(str(e))
        return jsonify(error=str(e)), 404

    tbl_benchmark.insert(benchmark)

    return jsonify(message='Not yet implemented.'), 200


#######################################################################################################################
# Object Functionality
#######################################################################################################################


def deserialize(obj):
    # TODO: Move this towards using a model decoder
    import providentia.repository.this.tbl_databases as db_table
    import providentia.repository.this.tbl_datasets as ds_table
    import providentia.repository.this.tbl_analysis as an_table

    if type(obj) is dict:
        benchmark = Benchmark()

        database = db_table.find_name(obj['database'])
        dataset = ds_table.find_name(obj['dataset'])
        analysis = an_table.find_name(obj['analysis'])

        if database is None:
            raise KeyError('Database "{}" is not supported!'.format(obj['database']))
        if dataset is None:
            raise KeyError('Dataset "{}" is not supported!'.format(obj['dataset']))
        if analysis is None:
            raise KeyError('Analysis "{}" is not supported!'.format(obj['analysis']))

        benchmark.database = database.database_id
        benchmark.dataset = dataset.dataset_id
        benchmark.analysis = analysis.analysis_id
        benchmark.title = obj['title']
        benchmark.description = obj['description']
        benchmark.query_time = 0
        benchmark.analysis_time = 0

        return benchmark
    else:
        raise NotImplementedError("Cannot deserialize data that is not dict or JSON format.")
