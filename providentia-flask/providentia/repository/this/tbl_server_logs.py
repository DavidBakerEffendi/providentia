import logging

from flask import current_app

from providentia.db.this import get_db
from providentia.models import server_log_decoder, ServerLog

TABLE = 'server_logs'
COLUMNS = ("id", "captured_at", "memory_perc")


def query_logs(from_date=None, to_date=None):
    where_condition = None

    if from_date is not None and to_date is not None:
        where_condition = "WHERE captured_at >= '{}'::timestamp AND captured_at <= '{}'::timestamp".format(from_date,
                                                                                                           to_date)
    elif from_date is not None:
        where_condition = "WHERE captured_at >= '{}'::timestamp".format(from_date)
    elif to_date is not None:
        where_condition = "WHERE captured_at <= '{}'::timestamp".format(to_date)

    with current_app.app_context():
        cur = get_db().cursor()
        if where_condition is not None:
            query = "SELECT id, captured_at, memory_perc FROM {} {} ORDER BY captured_at ASC".format(TABLE,
                                                                                                     where_condition)
        else:
            query = "SELECT id, captured_at, memory_perc FROM {} ORDER BY captured_at ASC".format(TABLE)

        cur.execute(query)
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

        cur.execute(query, (server_log.captured_at, server_log.memory_perc,))
        server_log.log_id = cur.fetchone()[0]
        conn.commit()
