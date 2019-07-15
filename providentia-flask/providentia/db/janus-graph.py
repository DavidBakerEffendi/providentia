from gremlin_python.driver import client

from providentia.globals import get_gremlin_client, set_gremlin_client


def connect():
    gremlin_client = get_gremlin_client()
    if gremlin_client is None:
        gremlin_client = client.Client('ws://localhost:8182/gremlin', 'g')
        set_gremlin_client(gremlin_client)

    return gremlin_client
