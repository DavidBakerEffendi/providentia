import logging


class Config(object):
    DEBUG = False
    TESTING = False
    DATABASE_URI = 'postgres://postgres:docker@127.0.0.1:5432/providentia'
    CORS_ORIGINS = ["http://localhost:4200"]
    LOGGING_LEVEL = logging.INFO


class ProductionConfig(Config):
    DEBUG = False
    DATABASE_URI = 'postgres://USERNAME:PASSWORD@HOST:PORT/providentia'
    SECRET_KEY = b'_5#y2L"F4Q8z\n\xec]/'
    CORS_ORIGINS = ["http://localhost:4200"]


class DevelopmentConfig(Config):
    DEBUG = True
    SECRET_KEY = 'dev'
    LOGGING_LEVEL = logging.DEBUG


class TestingConfig(Config):
    TESTING = True


def default_config():
    return DevelopmentConfig
