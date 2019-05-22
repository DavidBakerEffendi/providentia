from flask import Blueprint
from flask_cors import cross_origin
from providentia.db import get_db
import json

bp = Blueprint('urls', __name__,)


@bp.route("/")
@cross_origin()
def index():
    """Show all the posts, most recent first."""
    db = get_db()
    # posts = db.execute(
    #     "SELECT p.id, title, body, created, author_id, username"
    #     " FROM post p JOIN user u ON p.author_id = u.id"
    #     " ORDER BY created DESC"
    # ).fetchall()

    data = {"message": "Connection to Flask backend successful!"}

    return json.dumps(data), 200, {"Content-Type": "application/json"}

