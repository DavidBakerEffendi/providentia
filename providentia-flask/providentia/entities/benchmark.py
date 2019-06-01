import json
import logging
from flask import Blueprint, Response, jsonify
from config import default_config
from flask_cors import cross_origin

bp = Blueprint('benchmark', __name__, )
config = default_config()
logging.basicConfig(level=config.LOGGING_LEVEL)

#######################################################################################################################
# REST Controllers
#######################################################################################################################


@bp.route("/", methods=['GET'])
@cross_origin()
def result_get():
    import providentia.repository.benchmark_repository as bm_table
    """Show all the posts, most recent first."""
    try:
        results = bm_table.query_results(10)
    except Exception as e:
        logging.error("Error on GET /api/benchmark/: " + str(e))
        return jsonify(error="Unexpected error while querying database! The database is most likely down."), 500

    if results is None:
        return jsonify(error="No results in database, you can add more by navigating to 'New Job' in the sidebar."), 503

    # Serialize objects
    for i in range(len(results)):
        results[i] = results[i].json

    return Response(json.dumps(results, default=str), status=200, mimetype='application/json')


@bp.route("/<benchmark_id>", methods=['GET'])
@cross_origin()
def result_find(benchmark_id):
    import providentia.repository.benchmark_repository as bm_table
    """Show all the posts, most recent first."""
    try:
        benchmark = bm_table.find(benchmark_id)
    except Exception as e:
        logging.error(str(e))
        return Response({"message": "Unexpected error while querying database!"}, status=500)

    if benchmark is None:
        return Response({"message": "Benchmark not found!"}, status=404)

    return Response(json.dumps(benchmark.json, default=str), status=200, mimetype='application/json')

#######################################################################################################################
# Object Functionality
#######################################################################################################################


def deserialize(obj):
    import providentia.repository.databases_repository as db_table
    import providentia.repository.datasets_repository as ds_table
    import providentia.repository.analysis_repository as an_table

    if type(obj) is dict:
        benchmark = Benchmark()
        benchmark.benchmark_id = obj['id']
        benchmark.database = db_table.find(obj['database_id'])
        benchmark.dataset = ds_table.find(obj['dataset_id'])
        benchmark.analysis = an_table.find(obj['analysis_id'])
        benchmark.date_executed = obj['date_executed']
        benchmark.title = obj['title']
        benchmark.description = obj['description']
        benchmark.query_time = obj['query_time']
        benchmark.analysis_time = obj['analysis_time']
        return benchmark
    elif type(obj) is str:
        pass
    else:
        raise NotImplementedError("Cannot deserialize data that is not dict or JSON format.")


class Benchmark(object):

    def __init__(self):
        self.__benchmark_id = None
        self.__database = None
        self.__dataset = None
        self.__analysis = None
        self.__date_executed = None
        self.__title = None
        self.__description = None
        self.__query_time = None
        self.__analysis_time = None

    @property
    def benchmark_id(self):
        return self.__benchmark_id

    @benchmark_id.setter
    def benchmark_id(self, __benchmark_id):
        self.__benchmark_id = __benchmark_id

    @property
    def database(self):
        return self.__database

    @database.setter
    def database(self, __database):
        self.__database = __database

    @property
    def dataset(self):
        return self.__dataset

    @dataset.setter
    def dataset(self, __dataset):
        self.__dataset = __dataset

    @property
    def analysis(self):
        return self.__analysis

    @analysis.setter
    def analysis(self, __analysis):
        self.__analysis = __analysis

    @property
    def date_executed(self):
        return self.__date_executed

    @date_executed.setter
    def date_executed(self, __date_executed):
        self.__date_executed = __date_executed

    @property
    def title(self):
        return self.__title

    @title.setter
    def title(self, __title):
        self.__title = __title

    @property
    def description(self):
        return self.__description

    @description.setter
    def description(self, __description):
        self.__description = __description

    @property
    def query_time(self):
        return self.__query_time

    @query_time.setter
    def query_time(self, __query_time):
        self.__query_time = __query_time

    @property
    def analysis_time(self):
        return self.__analysis_time

    @analysis_time.setter
    def analysis_time(self, __analysis_time):
        self.__analysis_time = __analysis_time

    @property
    def json(self):
        out = dict()
        out['id'] = self.__benchmark_id
        out['database'] = self.__database.json
        out['dataset'] = self.__dataset.json
        out['analysis'] = self.__analysis.json
        out['date_executed'] = self.__date_executed
        out['title'] = self.__title
        out['description'] = self.__description
        out['query_time'] = self.__query_time
        out['analysis_time'] = self.__analysis_time

        return out

    def __str__(self):
        return json.dumps(self.json, default=str)
