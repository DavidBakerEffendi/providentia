import json
import logging
from datetime import datetime, timedelta

from flask import Blueprint, Response, request
from flask_cors import cross_origin

from providentia.models import model_encoder
from providentia.repository import tbl_server_logs

bp = Blueprint('logs', __name__, )


@bp.route("/", methods=['GET'])
@cross_origin()
def get_logs():
    """Get recent logs."""
    try:
        # Get results from 2 minutes ago till now
        results = tbl_server_logs.query_logs(from_date=datetime.utcnow() - timedelta(0, 120))
    except Exception as e:
        logging.error('Failed to retrieve logs from database: ', str(e))
        return Response(json.dumps({"message": "Unexpected error while querying database!"}), status=500)

    if results is None:
        return Response(json.dumps({"message": "Database empty."}), status=200)

    return Response(json.dumps(results, default=model_encoder), status=200, mimetype='application/json')


@bp.route("/", methods=['POST'])
@cross_origin()
def get_logs_from_to():
    """Get logs to and from certain points."""
    data = request.get_json()

    from_date = data['from']
    to_date = data['to']

    try:
        results = tbl_server_logs.query_logs(from_date, to_date)
    except Exception as e:
        logging.error('Failed to retrieve logs from database: ', str(e))
        return Response(json.dumps({"message": "Unexpected error while querying database!"}), status=500)

    if results is None:
        return Response(json.dumps({"message": "Database empty."}), status=503)

    return Response(json.dumps(results, default=model_encoder), status=200, mimetype='application/json')
