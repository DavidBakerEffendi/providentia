import logging

from flask import current_app

from providentia.db.this import get_db
from providentia.models import benchmark_decoder, Benchmark

TABLE = 'benchmarks'
COLUMNS = ("id", "database_id", "dataset_id", "analysis_id", "date_executed", "query_time", "analysis_time", "status")


def query_results(n=None):
    with current_app.app_context():
        cur = get_db().cursor()
        query = "SELECT id, database_id, dataset_id, analysis_id, date_executed, query_time, " \
                "analysis_time, status FROM {} ORDER BY date_executed DESC".format(TABLE)

        if n is None:
            cur.execute(query)
        else:
            cur.execute(query + " LIMIT %s", (str(n),))

        rows = []
        if cur.rowcount > 0:
            for row in cur.fetchall():
                rows.append(dict(zip(COLUMNS, row)))
        else:
            return None

        deserialized = [benchmark_decoder(row) for row in rows]

        return deserialized


def find(row_id):
    with current_app.app_context():
        cur = get_db().cursor()
        query = "SELECT id, database_id, dataset_id, analysis_id, date_executed, query_time, " \
                "analysis_time, status FROM {} WHERE id = %s".format(TABLE)

        cur.execute(query, (row_id,))

        if cur.rowcount > 0:
            result = dict(zip(COLUMNS, cur.fetchone()))
        else:
            return None

        deserialized = benchmark_decoder(result)

        return deserialized


def insert(benchmark):
    with current_app.app_context():
        insert_into = "INSERT INTO {} (".format(TABLE)
        values = "VALUES ("
        values_arr = []

        if benchmark.benchmark_id is not None:
            insert_into += "{}, ".format(COLUMNS[0])
            values += "%s::uuid, "
            values_arr.append(benchmark.benchmark_id)
        if benchmark.database is not None:
            insert_into += "{}, ".format(COLUMNS[1])
            values += "%s::uuid, "
            values_arr.append(benchmark.database)
        if benchmark.dataset is not None:
            insert_into += "{}, ".format(COLUMNS[2])
            values += "%s::uuid, "
            values_arr.append(benchmark.dataset)
        if benchmark.analysis is not None:
            insert_into += "{}, ".format(COLUMNS[3])
            values += "%s::uuid, "
            values_arr.append(benchmark.analysis)
        if benchmark.date_executed is not None:
            insert_into += "{}, ".format(COLUMNS[4])
            values += "%s, "
            values_arr.append(benchmark.date_executed)
        if benchmark.query_time is not None:
            insert_into += "{}, ".format(COLUMNS[5])
            values += "%s, "
            values_arr.append(benchmark.query_time)
        if benchmark.analysis_time is not None:
            insert_into += "{}, ".format(COLUMNS[6])
            values += "%s, "
            values_arr.append(benchmark.analysis_time)
        if benchmark.status is not None:
            insert_into += "{}, ".format(COLUMNS[7])
            values += "%s, "
            values_arr.append(benchmark.status)

        insert_into = insert_into[:-2] + ") "
        values = values[:-2] + ");"

        # execute and commit to reflect immediately for the job scheduler to see
        db = get_db()
        query = db.cursor().mogrify(insert_into + values, values_arr)
        logging.debug("Executing query: %s", query)
        db.cursor().execute(query, values_arr)
        db.commit()


def is_job_being_processed():
    with current_app.app_context():
        cur = get_db().cursor()
        query = "SELECT id FROM {} WHERE status = 'PROCESSING'".format(TABLE)
        cur.execute(query)

        if cur.rowcount > 0:
            return True
        else:
            return False


def get_unstarted_jobs():
    with current_app.app_context():
        cur = get_db().cursor()
        query = "SELECT id, database_id, dataset_id, analysis_id, date_executed, query_time, " \
                "analysis_time, status FROM {} WHERE status = 'WAITING'".format(TABLE)

        cur.execute(query)

        rows = []
        if cur.rowcount > 0:
            for row in cur.fetchall():
                rows.append(dict(zip(COLUMNS, row)))
        else:
            return None

        return [benchmark_decoder(row) for row in rows]


def set_as(benchmark_id, status):
    if status != 'WAITING' and status != 'PROCESSING' and status != 'COMPLETE':
        raise Exception('Status must be either WAITING, PROCESSING, or COMPLETE. Your input was "{}"', status)
    with current_app.app_context():
        conn = get_db()
        cur = conn.cursor()
        query = "UPDATE {} SET status = %s WHERE id = %s".format(TABLE)

        cur.execute(query, (status, benchmark_id,))
        logging.debug("Executed query: %s", cur.query)

        conn.commit()


def update(benchmark: Benchmark):
    with current_app.app_context():
        insert_into = "UPDATE {} SET ".format(TABLE)
        values_arr = []

        if benchmark.database is not None:
            insert_into += "{} = ".format(COLUMNS[1])
            insert_into += "%s::uuid, "
            values_arr.append(benchmark.database.database_id)
        if benchmark.dataset is not None:
            insert_into += "{} = ".format(COLUMNS[2])
            insert_into += "%s::uuid, "
            values_arr.append(benchmark.dataset.dataset_id)
        if benchmark.analysis is not None:
            insert_into += "{} = ".format(COLUMNS[3])
            insert_into += "%s::uuid, "
            values_arr.append(benchmark.analysis.analysis_id)
        if benchmark.date_executed is not None:
            insert_into += "{} = ".format(COLUMNS[4])
            insert_into += "%s, "
            values_arr.append(benchmark.date_executed)
        if benchmark.query_time is not None:
            insert_into += "{} = ".format(COLUMNS[5])
            insert_into += "%s, "
            values_arr.append(benchmark.query_time)
        if benchmark.analysis_time is not None:
            insert_into += "{} = ".format(COLUMNS[6])
            insert_into += "%s, "
            values_arr.append(benchmark.analysis_time)
        if benchmark.status is not None:
            insert_into += "{} = ".format(COLUMNS[7])
            insert_into += "%s, "
            values_arr.append(benchmark.status)

        # add where clause
        insert_into = insert_into[:-2] + " WHERE id = %s::uuid"
        values_arr.append(benchmark.benchmark_id)

        # execute and commit to reflect immediately for the job scheduler to see
        db = get_db()
        query = db.cursor().mogrify(insert_into, values_arr)
        logging.debug("Executing query: %s with %s", query, values_arr)
        db.cursor().execute(query, values_arr)
        db.commit()
