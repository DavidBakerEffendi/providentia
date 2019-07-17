import logging
from datetime import datetime, timedelta

from flask import current_app

from providentia.db.this import get_db
from providentia.models import server_log_decoder, ServerLog

TABLE = 'server_logs'
COLUMNS = ("id", "captured_at", "memory_perc")


def query_logs(date=None):
    try:
        datetime.strptime(date, '%Y-%m-%d %H:%M:%S.%f')
    except Exception as e:
        logging.debug('Could not convert given date %s, defaulting to %s. Error %s', date,
                      datetime.now() - timedelta(0, 60), str(e))
        date = datetime.now() - timedelta(0, 60)

    with current_app.app_context():
        cur = get_db().cursor()
        query = "SELECT id, captured_at, memory_perc FROM {} WHERE captured_at >= %s ORDER BY captured_at ASC".format(
            TABLE)

        cur.execute(query, (date, ))
        logging.debug("Executed: %s", cur.query)

        rows = []
        if cur.rowcount > 0:
            for row in cur.fetchall():
                rows.append(dict(zip(COLUMNS, row)))
        else:
            return None

        return [server_log_decoder(row) for row in rows]


def insert_log(server_log: ServerLog):
    with current_app.app_context():
        conn = get_db()
        cur = conn.cursor()
        query = "INSERT INTO {} (captured_at, memory_perc) VALUES (%s, %s) RETURNING id".format(TABLE)

        cur.execute(query, (server_log.captured_at, server_log.memory_perc, ))
        server_log.log_id = cur.fetchone()[0]
        conn.commit()
