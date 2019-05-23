import os
from flask_cors import CORS
from flask import Flask
from config import default_config

log = Flask.logger
config = default_config()

def create_app():
    """Create and configure an instance of the Flask application."""
    app = Flask(__name__)
    app.config.from_object('config.DevelopmentConfig')

    # ensure the instance folder exists
    try:
        os.makedirs(app.instance_path)
    except OSError:
        pass

    # register the database commands
    from providentia import db

    db.init_app(app)

    # apply the blueprints to Providentia
    from providentia.controllers import home
    from providentia.entities import result

    app.register_blueprint(result.bp, url_prefix='/api/result')
    app.register_blueprint(home.bp, url_prefix='/api/home')

    # register CORS
    CORS(app, resources={r"/api/*": config.CORS_ORIGINS})

    return app
