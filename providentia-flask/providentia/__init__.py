import atexit
import logging
import os

import nltk
from flask import Flask
from flask_cors import CORS

import config


def check_nltk_deps():
    try:
        nltk.data.find('tokenizers/punkt')
    except LookupError:
        nltk.download('punkt')
    try:
        nltk.data.find('corpora/stopwords')
    except LookupError:
        nltk.download('stopwords')
    try:
        nltk.data.find('taggers/averaged_perceptron_tagger')
    except LookupError:
        nltk.download('averaged_perceptron_tagger')


def test_database_connections(app):
    """Tests if a connection to databases can be established."""
    from providentia.db import janus_graph, postgres, tigergraph
    from providentia.repository import tbl_databases

    if janus_graph.test_connection(app):
        tbl_databases.set_status('JanusGraph', status='UP', app=app)
    else:
        tbl_databases.set_status('JanusGraph', status='DOWN', app=app)

    if postgres.test_connection(app):
        tbl_databases.set_status('PostgreSQL', status='UP', app=app)
    else:
        tbl_databases.set_status('PostgreSQL', status='DOWN', app=app)

    if tigergraph.test_connection(app):
        tbl_databases.set_status('TigerGraph', status='UP', app=app)
    else:
        tbl_databases.set_status('TigerGraph', status='DOWN', app=app)


def filter_apscheduler_logs():
    """Filters the excessive and unnecessary APScheduler logs"""

    class NoRunningFilter(logging.Filter):
        def filter(self, record):
            return not (record.msg.startswith('Running job') or
                        record.msg.startswith('Next wakeup') or
                        record.msg.startswith('Execution of job') or
                        record.msg.startswith('Looking for jobs to run') or
                        'executed successfully' in record.msg)

    class RequestFilter(logging.Filter):
        def filter(self, record):
            return not (record.msg.startswith('http') or
                        record.msg.startswith('Starting new HTTP connection'))
    
    run_filter = NoRunningFilter()
    req_filter = RequestFilter()
    logging.getLogger("apscheduler.scheduler").addFilter(run_filter)
    logging.getLogger("apscheduler.executors.default").addFilter(run_filter)
    logging.getLogger("urllib3.connectionpool").addFilter(req_filter)


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
    from providentia.views import home, new_job, dataset, database, benchmark, analysis, classifier, logs, queries, \
        kate, review_trends

    app.register_blueprint(benchmark.bp, url_prefix='/api/benchmark')
    app.register_blueprint(dataset.bp, url_prefix='/api/dataset')
    app.register_blueprint(database.bp, url_prefix='/api/database')
    app.register_blueprint(analysis.bp, url_prefix='/api/analysis')
    app.register_blueprint(home.bp, url_prefix='/api/home')
    app.register_blueprint(new_job.bp, url_prefix='/api/new-job')
    app.register_blueprint(classifier.bp, url_prefix='/api/classifier')
    app.register_blueprint(logs.bp, url_prefix='/api/logs')
    app.register_blueprint(queries.bp, url_prefix="/api/queries")
    app.register_blueprint(kate.bp, url_prefix="/api/result/kate")
    app.register_blueprint(review_trends.bp, url_prefix="/api/result/review-trends")

    # establish analysis database for this app
    logging.debug('Establishing database connections.')
    from providentia.db import this
    app.teardown_appcontext(this.close_db)

    # restart any incomplete jobs
    from providentia.repository.tbl_benchmark import reset_processing_jobs
    with app.app_context():
        reset_processing_jobs()

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
    scheduler.add_job(func=log_server_state, id='log_server_state', trigger='interval', seconds=1)
    # train the classifier model if it is enabled
    if app.config['ENABLE_SENTIMENT'] is True:
        logging.debug('[SENTIMENT] Checking necessary NLTK resources are installed')
        check_nltk_deps()
        # Check if model exists else train one
        if os.path.exists("./models/naivebayes.pickle") is True and os.path.exists("./models/features.pickle") is True:
            try:
                sentiment.deserialize_model()
                logging.info("Sentiment classifier ready!")
            except OSError as e:
                logging.error("Unable to deserialize Naive Bayes model! Creating a new one.", e)
                scheduler.add_job(func=sentiment.train_model, id='train_sentiment', trigger='date',
                                  next_run_time=classifier_start_train, args=[app.config['SENTIMENT_DATA'], app])
        else:
            scheduler.add_job(func=sentiment.train_model, id='train_sentiment', trigger='date',
                              next_run_time=classifier_start_train, args=[app.config['SENTIMENT_DATA'], app])
    filter_apscheduler_logs()
    scheduler.start()
    # shut down the scheduler when exiting the app
    atexit.register(lambda: scheduler.shutdown())

    return app


app = create_app()
