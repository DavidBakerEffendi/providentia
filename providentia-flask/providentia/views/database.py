import json
import logging

from flask import Blueprint, Response, request
from flask_cors import cross_origin

from providentia.repository import tbl_databases
from providentia.models import model_encoder

bp = Blueprint('database', __name__, )


@bp.route("/", methods=['GET'])
@cross_origin()
def result_get():
    """Show all the posts, most recent first."""
    try:
        results = tbl_databases.query_results()
    except Exception as e:
        logging.error(str(e))
        return Response({"message": "Unexpected error while querying database!"}, status=500)

    if results is None:
        return Response({"message": "Database empty."}, status=200)

    return Response(json.dumps(results, default=model_encoder), status=200, mimetype='application/json')


@bp.route("/query/<db_name>", methods=['POST'])
@cross_origin()
def query_db(db_name):
    """Submit query to db, return response."""
    from providentia.db import janus_graph, postgres
    from time import perf_counter_ns

    data = request.get_json()

    logging.debug('Incoming query for {}: {}'.format(db_name, str(data['query'])))

    query = data['query']
    if 'delete' in query.lower() or 'update' in query.lower() or 'drop' in query.lower() or 'add' in query.lower():
        return Response({"message": "You may only use select-style queries!"}, status=400)

    result = ''

    start = perf_counter_ns()
    if db_name == "JanusGraph":
        result = str(janus_graph.execute_query(query))
    elif db_name == "PostgreSQL":
        result = str(postgres.execute_query(query))
    elif db_name == "Cassandra":
        pass
    time_elapsed = (perf_counter_ns() - start) / 1000000

    logging.debug('Query response: {}'.format(result))
    return Response(json.dumps({'result': result, 'time': time_elapsed}), status=200, mimetype='application/json')
