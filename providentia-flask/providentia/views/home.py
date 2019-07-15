from flask import Blueprint, jsonify
from flask_cors import cross_origin

bp = Blueprint('home', __name__, )


@bp.route("/metrics", methods=['GET'])
@cross_origin()
def metrics():
    """Show all the posts, most recent first."""

    # TODO: Obtain database metrics from this DB and Spring etc

    return jsonify(message='Not yet implemented.'), 501
