import json

from flask import Blueprint, Response, request
from flask_cors import cross_origin

bp = Blueprint('classifier', __name__, )


@bp.route("/", methods=['GET'])
@cross_origin()
def test_status():
    """Tests whether the classifier is ready or not."""

    from providentia.classifier import sentiment
    if sentiment.get_classifier() is None:
        return Response('Classifier not ready!', status=503)
    else:
        return Response(json.dumps({"status": True}), status=200, mimetype='application/json')


@bp.route("/", methods=['POST'])
@cross_origin()
def classify():
    """Classify the given text"""

    data = request.get_json()
    text = data['text']

    result = {}
    from providentia.classifier import sentiment
    if sentiment.get_classifier() is None:
        return Response({"message": "Classifier not ready yet!"}, status=503)
    else:
        result['result'] = sentiment.classify(text)

    return Response(json.dumps(result), status=200, mimetype='application/json')
