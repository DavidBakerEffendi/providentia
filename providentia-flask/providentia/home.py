from flask import Blueprint
import json
from providentia.db import get_db

bp = Blueprint('urls', __name__,)


@bp.route("/")
def index():
    """Show all the posts, most recent first."""
    db = get_db()
    # posts = db.execute(
    #     "SELECT p.id, title, body, created, author_id, username"
    #     " FROM post p JOIN user u ON p.author_id = u.id"
    #     " ORDER BY created DESC"
    # ).fetchall()

    return "Connection to Flask backend successful!", 200, {"Content-Type": "application/json"}

