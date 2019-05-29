import logging
import json
from flask import Blueprint, Response
from flask_cors import cross_origin
from config import default_config
from providentia.repository.analysis_repository import *

bp = Blueprint('analysis', __name__, )
config = default_config()
logging.basicConfig(level=config.LOGGING_LEVEL)

#######################################################################################################################
# REST Controllers
#######################################################################################################################


@bp.route("/", methods=['GET'])
@cross_origin()
def result_get():
    """Show all the posts, most recent first."""
    try:
        results = query_results()
    except Exception as e:
        logging.error(str(e))
        return Response({"message": "Unexpected error while querying database!"}, status=500)

    if results is None:
        return Response({"message": "Database empty."}, status=200)

    # Serialize objects
    for i in range(len(results)):
        results[i] = results[i].json

    return Response(json.dumps(results, default=str), status=200, mimetype='application/json')

#######################################################################################################################
# Object Functionality
#######################################################################################################################


def deserialize(obj):
    import providentia.repository.datasets_repository as ds_table

    if type(obj) is dict:
        analysis = Analysis()
        analysis.analysis_id = obj['id']
        analysis.dataset = ds_table.find(obj['dataset_id'])
        analysis.name = obj['name']
        analysis.description = obj['description']
        return analysis
    elif type(obj) is str:
        raise NotImplementedError("Not implemented for JSON string yet.")
    else:
        raise NotImplementedError("Cannot deserialize data that is not dict or JSON format.")


class Analysis(object):

    def __init__(self):
        self.__analysis_id = None
        self.__dataset = None
        self.__name = None
        self.__description = None

    @property
    def analysis_id(self):
        return self.__analysis_id

    @analysis_id.setter
    def analysis_id(self, __analysis_id):
        self.__analysis_id = __analysis_id

    @property
    def dataset(self):
        return self.__dataset

    @dataset.setter
    def dataset(self, __dataset):
        self.__dataset = __dataset

    @property
    def name(self):
        return self.__name

    @name.setter
    def name(self, __name):
        self.__name = __name

    @property
    def description(self):
        return self.__description

    @description.setter
    def description(self, __description):
        self.__description = __description

    @property
    def json(self):
        out = dict()
        out['id'] = self.__analysis_id
        out['dataset'] = self.__dataset.json
        out['name'] = self.__name
        out['description'] = self.__description

        return out

    def __str__(self):
        import json

        return json.dumps(self.json, default=str)
