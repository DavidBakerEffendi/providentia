import logging
from datetime import datetime
from time import perf_counter_ns

from providentia.classifier import sentiment
from providentia.db import janus_graph, postgres, tigergraph
from providentia.models import Benchmark, CitySentimentResult
from providentia.repository.analysis_tables import tbl_city_sentiment

analysis_id = "05c2c642-32c0-4e6a-a0e5-c53028035fc8"
julie_id = "7weuSPSSqYLUFga6IYP4pg"

coords = (36.16, -115.14)


def run(benchmark: Benchmark):
    database = benchmark.database.name
    logging.debug("Starting city sentiment analysis using %s", database)
    # initialize timers
    benchmark.date_executed = datetime.utcnow()
    # Run query
    start = perf_counter_ns()
    reviews = get_lv_reviews_from_friends(database)
    q1_total_time = (perf_counter_ns() - start) / 1000000
    if database == "PostgreSQL":
        reviews = [{'text': i[0], 'stars': i[1]} for i in reviews]
    # Analyse results
    start = perf_counter_ns()
    vegas_reviews = VegasReviews()
    for r in reviews:
        vegas_reviews.add_review(r['text'], r['stars'])
    analysis_time = (perf_counter_ns() - start) / 1000000
    # Add time
    total_time = q1_total_time + analysis_time
    logging.info('\n===== Completed Phoenix Review Trend analysis =====\n'
                 'Database:\t\t%s\n'
                 'Total query time:\t%.2f ms\n'
                 'Time analysing:\t\t%.2f ms\n'
                 '------------------------------------\n'
                 'Total time:\t\t%.2f ms\n',
                 database, q1_total_time, analysis_time, total_time)
    # Update benchmark object values
    benchmark.query_time = q1_total_time
    benchmark.analysis_time = analysis_time
    # Insert results into database
    city_sentiment = CitySentimentResult()
    city_sentiment.benchmark = benchmark
    l = [vegas_reviews.get_stars(), vegas_reviews.get_sentiment()]

    def scale_column(l: list):
        """From the given list, returns the scaled version of the list."""
        min_v = l[0]
        max_v = l[0]
        for e in l:
            if min_v > e:
                min_v = e
            if max_v < e:
                max_v = e
        nl = []
        for x in l:
            new_val = (x - min_v) / float(max_v - min_v)
            nl.append(new_val)
        return nl

    l = scale_column(l)
    city_sentiment.stars = l[0]
    city_sentiment.sentiment = l[1]

    tbl_city_sentiment.insert(city_sentiment)


def get_lv_reviews_from_friends(database):
    lat, lon = coords
    result = []
    if database == 'JanusGraph':
        result = janus_graph.execute_query(
            'g.V().has("User", "user_id", "%s").as("julie")'
            '.out("FRIENDS").as("f1").out("FRIENDS").as("f2")'
            '.union(select("f1"), select("f2")).dedup().where(neq("julie")).outE("REVIEWS").filter{'
            'it.get().value("date").atZone(ZoneId.of("-07:00")).toLocalDate().getMonthValue() >= 11 &&'
            'it.get().value("date").atZone(ZoneId.of("-07:00")).toLocalDate().getMonthValue() <= 12}'
            '.as("text").as("stars")'
            '.inV().has("location", geoWithin(Geoshape.circle(%f, %f, 30)))'
            '.select("text", "stars").by("text").by("stars")' % (julie_id, lat, lon))
    elif database == "PostgreSQL":
        result = postgres.execute_query(
            "SELECT DISTINCT R.text, R.stars FROM review R "
            "JOIN business B ON R.business_Id = B.id "
            "INNER JOIN friends F2 ON R.user_id = F2.friend_id "
            "INNER JOIN friends F1 ON F2.user_id = F1.friend_id "
            "WHERE F1.user_id = $${}$$ "
            "AND F2.user_id <> $${}$$ "
            "AND (R.user_id = F1.user_id OR R.user_id = F1.friend_id) "
            "AND ST_DWithin(B.location, ST_MakePoint({}, {})::geography, 30000) "
            "AND (date_part('month', R.date) >= 11 AND date_part('month', R.date) <= 12)"
                .format(julie_id, julie_id, lon, lat))
    elif database == "TigerGraph":
        req = tigergraph.execute_query('getFriendReviewsInArea?p={}&lat={}&lon={}'.format(julie_id, lat, lon))
        if req is not None:
            result = req[0]['@@reviews']
    return result


class VegasReviews(object):

    def __init__(self):
        self.review_count = 0
        self.stars = 0
        self.positive_count = 0
        self.negative_count = 0

    def add_review(self, text, stars):
        self.review_count += 1
        self.stars += stars
        if sentiment.classify(text) == "pos":
            self.positive_count += 1
        else:
            self.negative_count += 1

    def get_sentiment(self):
        return float((self.positive_count / self.review_count) * 100)

    def get_stars(self):
        return float(self.stars / self.review_count)

    def __str__(self):
        return "{{'stars': '{}', 'sentiment':{}}}" \
            .format(self.get_stars(), self.get_sentiment())
