import logging

from flask import current_app

from providentia.db.this import get_db
from providentia.models import cpu_log_decoder, CPULog

TABLE = 'cpu_logs'
COLUMNS = ("id", "system_log_id", "core_id", "cpu_perc")


def query_log(system_log_id):
    with current_app.app_context():
        cur = get_db().cursor()
        query = "SELECT id, system_log_id, core_id, cpu_perc FROM {} WHERE system_log_id = %s ORDER BY core_id ASC".format(
            TABLE)

        cur.execute(query, (system_log_id, ))
        # logging.debug("Executed: %s", cur.query)

        rows = []
        if cur.rowcount > 0:
            for row in cur.fetchall():
                rows.append(dict(zip(COLUMNS, row)))
        else:
            return None

        return [cpu_log_decoder(row) for row in rows]


def insert_log(cpu_log: CPULog):
    with current_app.app_context():
        conn = get_db()
        cur = conn.cursor()
        query = "INSERT INTO {} (system_log_id, core_id, cpu_perc) VALUES (%s, %s, %s) RETURNING id".format(TABLE)

        cur.execute(query, (cpu_log.system_log_id, cpu_log.core_id, cpu_log.cpu_perc, ))
        cpu_log.log_id = cur.fetchone()[0]
        conn.commit()
