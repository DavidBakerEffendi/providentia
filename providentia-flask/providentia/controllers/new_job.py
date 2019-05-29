import logging
import json
from flask import Blueprint, Response, jsonify, request
from flask_cors import cross_origin
from config import default_config

bp = Blueprint('new-job', __name__, )
config = default_config()
logging.basicConfig(level=config.LOGGING_LEVEL)


#######################################################################################################################
# REST Controllers
#######################################################################################################################


@bp.route("/", methods=['POST'])
@cross_origin(allow_headers=['Content-Type'])
def new_job():
    """Capture new job to begin processing."""
    data = request.form
    logging.debug(data)
    # TODO: Obtain database metrics from this DB and Spring etc

    return jsonify(message='Not yet implemented.'), 200


#######################################################################################################################
# Object Functionality
#######################################################################################################################


def deserialize(obj):
    pass
