import atexit
import logging
import os

from flask import Flask
from flask_cors import CORS

import config


def test_database_connections(app):
    from providentia.db import janus_graph, postgres
    from providentia.repository import tbl_databases

    if janus_graph.test_connection(app):
        tbl_databases.set_status('JanusGraph', status='UP', app=app)
    else:
        tbl_databases.set_status('JanusGraph', status='DOWN', app=app)

    if postgres.test_connection(app):
        tbl_databases.set_status('PostgreSQL', status='UP', app=app)
    else:
        tbl_databases.set_status('PostgreSQL', status='DOWN', app=app)


def create_app():
    """Create and configure an instance of the Flask application."""
    app = Flask(__name__, instance_relative_config=True)
    app.config.from_object(config)
    app.config.from_pyfile('config.py')

    # set logging level
    logging.basicConfig(level=app.config['LOGGING_LEVEL'])

    # ensure the instance folder exists
    try:
        os.makedirs(app.instance_path)
    except OSError:
        pass

    # register the database commands
    from providentia.db import this as db
    db.init_app(app)

    # apply the blueprints to Providentia
    logging.debug('Applying blueprints to routes.')
    from providentia.views import home, new_job, dataset, database, benchmark, analysis, classifier, logs, queries

    app.register_blueprint(benchmark.bp, url_prefix='/api/benchmark')
    app.register_blueprint(dataset.bp, url_prefix='/api/dataset')
    app.register_blueprint(database.bp, url_prefix='/api/database')
    app.register_blueprint(analysis.bp, url_prefix='/api/analysis')
    app.register_blueprint(home.bp, url_prefix='/api/home')
    app.register_blueprint(new_job.bp, url_prefix='/api/new-job')
    app.register_blueprint(classifier.bp, url_prefix='/api/classifier')
    app.register_blueprint(logs.bp, url_prefix='/api/logs')
    app.register_blueprint(queries.bp, url_prefix="/api/queries")

    # establish analysis database for this app
    logging.debug('Establishing database connections.')
    from providentia.db import this
    app.teardown_appcontext(this.close_db)
    # test connections to benchmark databases
    test_database_connections(app)

    # register CORS
    logging.debug('Registering CORS filter.')
    CORS(app, resources={r"/api/*": {"origins": app.config['CORS_ORIGINS']}})

    # enable scheduler
    logging.debug('Starting background jobs.')
    from apscheduler.schedulers.background import BackgroundScheduler
    from providentia.analysis.periodic_jobs import log_server_state, execute_waiting
    from providentia.classifier import sentiment, fake
    from datetime import datetime, timedelta

    classifier_start_train = datetime.now() + timedelta(0, 10)

    scheduler = BackgroundScheduler()
    scheduler.add_job(func=execute_waiting, id='execute_waiting', trigger='interval', seconds=10)

    # Test for analysis
    # test_analysis = datetime.now() + timedelta(0, 5)
    # scheduler.add_job(func=execute_waiting, id='execute_waiting', trigger='date',next_run_time=test_analysis)

    scheduler.add_job(func=log_server_state, id='log_server_state', trigger='interval', seconds=2.5)
    # train the classifier models if they are enabled
    if app.config['ENABLE_SENTIMENT'] is True:
        scheduler.add_job(func=sentiment.train_model, id='train_sentiment', trigger='date',
                          next_run_time=classifier_start_train, args=[app.config['SENTIMENT_DATA'], app])
    if app.config['ENABLE_FAKE'] is True:
        scheduler.add_job(func=fake.train_model, id='train_fake', trigger='date',
                          next_run_time=classifier_start_train, args=[app.config['FAKE_DATA'], app])
    scheduler.start()
    # shut down the scheduler when exiting the app
    atexit.register(lambda: scheduler.shutdown())

    return app


app = create_app()
