import logging
import json
from flask import Blueprint, Response
from flask_cors import cross_origin
from config import default_config
from providentia.repository.databases_repository import *

bp = Blueprint('database', __name__, )
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
    if type(obj) is dict:
        database = Database()
        database.database_id = obj['id']
        database.name = obj['name']
        database.description = obj['description']
        database.icon = obj['icon']
        return database
    elif type(obj) is str:
        raise NotImplementedError("Not implemented for JSON string yet.")
    else:
        raise NotImplementedError("Cannot deserialize data that is not dict or JSON format.")


class Database(object):

    def __init__(self):
        self.__database_id = None
        self.__name = None
        self.__description = None
        self.__icon = None

    @property
    def database_id(self):
        return self.__database_id

    @database_id.setter
    def database_id(self, __database_id):
        self.__database_id = __database_id

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
    def icon(self):
        return self.__icon

    @icon.setter
    def icon(self, __icon):
        self.__icon = __icon

    @property
    def json(self):
        out = dict()
        out['id'] = self.__database_id
        out['name'] = self.__name
        out['description'] = self.__description
        out['icon'] = self.__icon

        return out

    def __str__(self):
        import json

        return json.dumps(self.json, default=str)
