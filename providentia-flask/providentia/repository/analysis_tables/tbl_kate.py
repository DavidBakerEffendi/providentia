from flask import current_app

from providentia.db.this import get_db
from providentia.models import kate_decoder, KateResult

TABLE = 'kate_analysis'
COLUMNS = ("id", "benchmark_id", "business", "sentiment_average", "star_average")


def get_results(benchmark_id):
    with current_app.app_context():
        cur = get_db().cursor()
        query = "SELECT id, benchmark_id, business, sentiment_average, star_average " \
                "FROM {} WHERE benchmark_id = %s::uuid".format(TABLE)

        cur.execute(query, (benchmark_id,))

        rows = []
        if cur.rowcount > 0:
            for row in cur.fetchall():
                rows.append(dict(zip(COLUMNS, row)))
        else:
            return None

        return [kate_decoder(row) for row in rows]


def insert(kate: KateResult):
    with current_app.app_context():
        insert_into = "INSERT INTO {} (".format(TABLE)
        values = "VALUES ("
        values_arr = []

        if kate.id is not None:
            insert_into += "{}, ".format(COLUMNS[0])
            values += "%s::uuid, "
            values_arr.append(kate.id)
        if kate.benchmark is not None:
            insert_into += "{}, ".format(COLUMNS[1])
            values += "%s::uuid, "
            values_arr.append(kate.benchmark.benchmark_id)
        if kate.business is not None:
            insert_into += "{}, ".format(COLUMNS[2])
            values += "%s, "
            values_arr.append(kate.business)
        if kate.sentiment_average is not None:
            insert_into += "{}, ".format(COLUMNS[3])
            values += "%s, "
            values_arr.append(kate.sentiment_average)
        if kate.star_average is not None:
            insert_into += "{}, ".format(COLUMNS[4])
            values += "%s, "
            values_arr.append(kate.star_average)

        insert_into = insert_into[:-2] + ") "
        values = values[:-2] + ");"

        # execute and commit
        db = get_db()
        query = db.cursor().mogrify(insert_into + values, values_arr)
        db.cursor().execute(query, values_arr)
        db.commit()
