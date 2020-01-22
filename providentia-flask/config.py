import logging

# Some variables for convenience. This assumes everything is deployed on the same machine.
HOSTNAME = '192.168.8.101'
CLIENT_PORT = '4200'
POSTGRES_PORT = '5432'
JANUS_PORT = '8182'
TIGER_PORT = '9000'

# Default
DEBUG = False
TESTING = False
DATABASE_URI = 'postgres://postgres:docker@{}:{}/providentia'.format(HOSTNAME, POSTGRES_PORT)
LOGGING_LEVEL = logging.INFO
CORS_ORIGINS = ["http://localhost:{}".format(CLIENT_PORT), "http://{}:{}".format(HOSTNAME, CLIENT_PORT)]

# Dev related variables. Comment out for prod
# LOGGING_LEVEL = logging.DEBUG
# DEBUG = False

ENABLE_SENTIMENT = True
SENTIMENT_PERC_DATA = 0.5  # Important for development, don't want to have to load all data every reload when re-training
SENTIMENT_DATA = './training-data/sentiment'

# The following should be added to ./instance/config.py

# Config for databases to benchmark
# POSTGRES_YELP_CONN = 'postgres://postgres:docker@{}:{}/'.format(HOSTNAME, POSTGRES_PORT)
# JANUSGRAPH_YELP_CONN = 'ws://{}:{}/gremlin'.format(HOSTNAME, JANUS_PORT)
# TIGERGRAPH_YELP_CONN = 'http://{}:{}/'.format(HOSTNAME, TIGER_PORT)
