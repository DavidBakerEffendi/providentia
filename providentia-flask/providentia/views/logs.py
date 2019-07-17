import json
import logging

from flask import Blueprint, Response
from flask_cors import cross_origin

from providentia.repository.this import tbl_server_logs
from providentia.models import model_encoder

bp = Blueprint('logs', __name__, )


@bp.route("/", methods=['GET'])
@cross_origin()
def get_logs():
    """Get recent logs."""
    try:
        results = tbl_server_logs.query_logs()
    except Exception as e:
        logging.error('Failed to retrieve logs from database: ', str(e))
        return Response({"message": "Unexpected error while querying database!"}, status=500)

    if results is None:
        return Response({"message": "Database empty."}, status=200)

    return Response(json.dumps(results, default=model_encoder), status=200, mimetype='application/json')
