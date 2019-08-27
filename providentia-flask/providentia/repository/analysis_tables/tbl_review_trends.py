from flask import current_app

from providentia.db.this import get_db
from providentia.models import review_trend_decoder, ReviewTrendResult

TABLE = 'review_trend_analysis'
COLUMNS = ("id", "benchmark_id", "stars", "length", "cool", "funny", "useful", "sentiment")


def get_results(benchmark_id):
    with current_app.app_context():
        cur = get_db().cursor()
        query = "SELECT id, benchmark_id, stars, length, cool, funny, useful, sentiment " \
                "FROM {} WHERE benchmark_id = %s::uuid".format(TABLE)

        cur.execute(query, (benchmark_id,))

        rows = []
        if cur.rowcount > 0:
            for row in cur.fetchall():
                rows.append(dict(zip(COLUMNS, row)))
        else:
            return None

        return [review_trend_decoder(row) for row in rows]


def insert(review_trend: ReviewTrendResult):
    with current_app.app_context():
        insert_into = "INSERT INTO {} (".format(TABLE)
        values = "VALUES ("
        values_arr = []

        if review_trend.id is not None:
            insert_into += "{}, ".format(COLUMNS[0])
            values += "%s::uuid, "
            values_arr.append(review_trend.id)
        if review_trend.benchmark is not None:
            insert_into += "{}, ".format(COLUMNS[1])
            values += "%s::uuid, "
            values_arr.append(review_trend.benchmark.benchmark_id)
        if review_trend.stars is not None:
            insert_into += "{}, ".format(COLUMNS[2])
            values += "%s, "
            values_arr.append(review_trend.stars)
        if review_trend.length is not None:
            insert_into += "{}, ".format(COLUMNS[3])
            values += "%s, "
            values_arr.append(review_trend.length)
        if review_trend.cool is not None:
            insert_into += "{}, ".format(COLUMNS[4])
            values += "%s, "
            values_arr.append(review_trend.cool)
        if review_trend.funny is not None:
            insert_into += "{}, ".format(COLUMNS[5])
            values += "%s, "
            values_arr.append(review_trend.funny)
        if review_trend.useful is not None:
            insert_into += "{}, ".format(COLUMNS[6])
            values += "%s, "
            values_arr.append(review_trend.useful)
        if review_trend.sentiment is not None:
            insert_into += "{}, ".format(COLUMNS[7])
            values += "%s, "
            values_arr.append(review_trend.sentiment)

        insert_into = insert_into[:-2] + ") "
        values = values[:-2] + ");"

        # execute and commit
        db = get_db()
        query = db.cursor().mogrify(insert_into + values, values_arr)
        db.cursor().execute(query, values_arr)
        db.commit()
