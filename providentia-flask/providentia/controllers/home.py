import logging
import json
from flask import Blueprint, Response, jsonify
from flask_cors import cross_origin
from config import default_config

bp = Blueprint('home', __name__, )
config = default_config()
logging.basicConfig(level=config.LOGGING_LEVEL)

#######################################################################################################################
# REST Controllers
#######################################################################################################################


@bp.route("/metrics", methods=['GET'])
@cross_origin()
def metrics():
    """Show all the posts, most recent first."""

    # TODO: Obtain database metrics from this DB and Spring etc

    return jsonify(message='Not yet implemented.'), 501
