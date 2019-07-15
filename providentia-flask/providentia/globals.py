gremlin_client = None


# TODO Postgres yelp connection
# TODO Figure out cassandra connection to es

def get_gremlin_client():
    global gremlin_client
    return gremlin_client


def set_gremlin_client(conn):
    global gremlin_client
    gremlin_client = conn
