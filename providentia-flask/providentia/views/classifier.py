import json

from flask import Blueprint, Response, request
from flask_cors import cross_origin

bp = Blueprint('classifier', __name__, )


@bp.route("/<mode>", methods=['GET'])
@cross_origin()
def test_status(mode: str):
    """Tests whether the classifier is ready or not."""
    mode = mode.lower()
    if mode != 'sentiment' and mode != 'fake':
        return Response({"Please specify a valid mode, e.g. /api/classifier/sentiment"}, status=400)

    from providentia.classifier import sentiment
    if mode == 'sentiment' and sentiment.get_classifier() is None:
        return Response('{} classifier not ready!'.format(mode.title()), status=503)
    else:
        return Response(json.dumps({"status": True}), status=200, mimetype='application/json')


@bp.route("/<mode>", methods=['POST'])
@cross_origin()
def classify(mode: str):
    """Classify the given text with the chosen mode"""
    mode = mode.lower()
    if mode != 'sentiment' and mode != 'fake':
        return Response({"message": "Please specify a valid mode, e.g. /api/classifier/sentiment"}, status=400)

    data = request.get_json()
    text = data['text']

    result = {}
    from providentia.classifier import sentiment
    if mode == 'sentiment' and sentiment.get_classifier() is None:
        return Response({"message": "{} classifier not ready yet!".format(mode)}, status=503)
    elif mode == 'sentiment':
        result['result'] = sentiment.classify(text)
    elif mode == 'fake':
        # TODO
        pass

    return Response(json.dumps(result), status=200, mimetype='application/json')
