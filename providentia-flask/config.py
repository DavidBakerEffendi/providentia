import logging

# Default
DEBUG = False
TESTING = False
DATABASE_URI = 'postgres://postgres:docker@127.0.0.1:5432/providentia'
LOGGING_LEVEL = logging.INFO

# Example prod
# DEBUG = False
# DATABASE_URI = 'postgres://USERNAME:PASSWORD@www.providentia.com:PORT/providentia'
# SECRET_KEY = b'_5#y2L"F4Q8z\n\xec]/'
# CORS_ORIGINS = ["http://www.providentia.com:4200"]

# Example dev
# DEBUG = True
# SECRET_KEY = 'dev'
# CORS_ORIGINS = ["http://localhost:4200"]
# DATABASE_URI = 'postgres://postgres:docker@127.0.0.1:5432/providentia'
# LOGGING_LEVEL = logging.DEBUG

# Example testing
# TESTING = True

