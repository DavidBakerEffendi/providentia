from flask import current_app

from providentia.db.this import get_db
from providentia.models import city_sentiment_decoder, CitySentimentResult

TABLE = 'city_analysis'
COLUMNS = ("id", "benchmark_id", "stars", "sentiment")


def get_result(benchmark_id):
    with current_app.app_context():
        cur = get_db().cursor()
        query = "SELECT id, benchmark_id, stars, sentiment " \
                "FROM {} WHERE benchmark_id = %s::uuid".format(TABLE)

        cur.execute(query, (benchmark_id,))

        if cur.rowcount > 0:
            result = dict(zip(COLUMNS, cur.fetchone()))
        else:
            return None

        return city_sentiment_decoder(result)


def insert(review_trend: CitySentimentResult):
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
        if review_trend.sentiment is not None:
            insert_into += "{}, ".format(COLUMNS[3])
            values += "%s, "
            values_arr.append(review_trend.sentiment)

        insert_into = insert_into[:-2] + ") "
        values = values[:-2] + ");"

        # execute and commit
        db = get_db()
        query = db.cursor().mogrify(insert_into + values, values_arr)
        db.cursor().execute(query, values_arr)
        db.commit()
