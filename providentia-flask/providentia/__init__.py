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
    from providentia.controllers import home, new_job
    from providentia.entities import benchmark, dataset, database, analysis

    app.register_blueprint(benchmark.bp, url_prefix='/api/benchmark')
    app.register_blueprint(dataset.bp, url_prefix='/api/dataset')
    app.register_blueprint(database.bp, url_prefix='/api/database')
    app.register_blueprint(analysis.bp, url_prefix='/api/analysis')
    app.register_blueprint(home.bp, url_prefix='/api/home')
    app.register_blueprint(new_job.bp, url_prefix='/api/new-job')

    # register CORS
    CORS(app, resources={r"/api/*": {"origins": config.CORS_ORIGINS}})

    return app
