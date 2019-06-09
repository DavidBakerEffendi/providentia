import providentia.db
import providentia.entities.benchmark
import logging
from flask import current_app
from config import default_config

config = default_config()
logging.basicConfig(level=config.LOGGING_LEVEL)

TABLE = 'benchmarks'
COLUMNS = ("id", "database_id", "dataset_id", "analysis_id", "date_executed", "title", "description", "query_time",
           "analysis_time")


def query_results(n=None):
    with current_app.app_context():
        cur = providentia.db.get_db().cursor()
        query = "SELECT id, database_id, dataset_id, analysis_id, date_executed, title, description, query_time, " \
                "analysis_time FROM {} ORDER BY date_executed DESC, title ASC".format(TABLE)

        if n is None:
            logging.debug("Executing query: %s", query)
            cur.execute(query)
        else:
            logging.debug("Executing query: %s LIMIT %d", query, n)
            cur.execute(query + " LIMIT %s", (str(n), ))

        rows = []
        if cur.rowcount > 0:
            for row in cur.fetchall():
                rows.append(dict(zip(COLUMNS, row)))
        else:
            return None

        deserialized = []

        for row in rows:
            deserialized.append(providentia.entities.benchmark.deserialize(row))

        return deserialized


def find(row_id):
    with current_app.app_context():
        cur = providentia.db.get_db().cursor()
        query = "SELECT id, database_id, dataset_id, analysis_id, date_executed, title, description, query_time, " \
                "analysis_time FROM {} WHERE id = %s".format(TABLE)

        logging.debug("Executing query: %s", query.replace('%s', '{}').format(row_id))
        cur.execute(query, (row_id,))

        if cur.rowcount > 0:
            result = dict(zip(COLUMNS, cur.fetchone()))
        else:
            return None

        deserialized = providentia.entities.benchmark.deserialize(result)

        return deserialized


def find_title(row_title):
    with current_app.app_context():
        cur = providentia.db.get_db().cursor()
        query = "SELECT id, database_id, dataset_id, analysis_id, date_executed, title, description, query_time, " \
                "analysis_time FROM {} WHERE title = %s".format(TABLE)

        logging.debug("Executing query: %s", query.replace('%s', '{}').format(row_title))
        cur.execute(query, (row_title,))

        if cur.rowcount > 0:
            result = dict(zip(COLUMNS, cur.fetchone()))
        else:
            return None

        deserialized = providentia.entities.benchmark.deserialize(result)

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
            values += "%s::timestamp, "
            values_arr.append(benchmark.date_executed)
        if benchmark.title is not None:
            insert_into += "{}, ".format(COLUMNS[5])
            values += "%s, "
            values_arr.append(benchmark.title)
        if benchmark.description is not None and len(benchmark.description) > 0:
            insert_into += "{}, ".format(COLUMNS[6])
            values += "%s, "
            values_arr.append(benchmark.description)
        if benchmark.query_time is not None:
            insert_into += "{}, ".format(COLUMNS[7])
            values += "%s, "
            values_arr.append(benchmark.query_time)
        if benchmark.analysis_time is not None:
            insert_into += "{}, ".format(COLUMNS[8])
            values += "%s, "
            values_arr.append(benchmark.analysis_time)

        insert_into = insert_into[:-2] + ") "
        values = values[:-2] + ");"

        # execute and commit to reflect immediately for the job scheduler to see
        db = providentia.db.get_db()
        query = db.cursor().mogrify(insert_into + values, values_arr)
        logging.debug("Executing query: %s", query)
        db.cursor().execute(query, values_arr)
        db.commit()


def get_unstarted_jobs():
    with current_app.app_context():
        cur = providentia.db.get_db().cursor()
        query = "SELECT id, database_id, dataset_id, analysis_id, date_executed, title, description, query_time, " \
                "analysis_time FROM {} WHERE date_executed IS NULL".format(TABLE)

        logging.debug("Executing query: %s", query)
        cur.execute(query)

        rows = []
        if cur.rowcount > 0:
            for row in cur.fetchall():
                rows.append(dict(zip(COLUMNS, row)))
        else:
            return None

        deserialized = []

        for row in rows:
            deserialized.append(providentia.entities.benchmark.deserialize(row))

        return deserialized
