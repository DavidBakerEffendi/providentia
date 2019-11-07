import logging
from datetime import datetime
from time import perf_counter_ns

from providentia.classifier import sentiment
from providentia.db import janus_graph, postgres
from providentia.models import Benchmark, KateResult
from providentia.repository import tbl_kate

kate_id = "qUL3CdRRF1vedNvaq06rIA"
analysis_id = "81c1ab05-bb06-47ab-8a37-b9aeee625d0f"


def run(benchmark: Benchmark):
    database = benchmark.database.name
    logging.debug("Starting Kate analysis using %s", database)
    # initialize timers
    benchmark.date_executed = datetime.utcnow()
    q1_total_time, q2_total_time, analysis_time, total_time = 0, 0, 0, 0
    # get similar users
    similar_users, q1_total_time = get_users_who_like_same_restaurants_as_kate(database)

    business_reviews = []
    for user_id in similar_users:
        recent_reviews, q2_time = get_recent_reviews_for_user_near_kate(database, user_id)
        # now add each user's rating and sentiment to a list of BusinessReview objects. the adding and classification
        # is handled in the constructor
        analysis_time += update_business_reviews(database, recent_reviews, business_reviews)

        # add up timers
        q2_total_time += q2_time
        total_time = q1_total_time + q2_total_time + analysis_time

    logging.info('\n===== Completed Kate analysis =====\n'
                 'Database:\t\t%s\n'
                 'Time for query 1:\t%.2f ms\n'
                 'Time for query 2:\t%.2f ms\n'
                 'Total query time:\t%.2f ms\n'
                 'Time analysing:\t\t%.2f ms\n'
                 '------------------------------------\n'
                 'Total time:\t\t%.2f ms\n',
                 database, q1_total_time, q2_total_time, q1_total_time + q2_total_time, analysis_time,
                 total_time)
    # Update benchmark object values
    benchmark.query_time = q1_total_time + q2_total_time
    benchmark.analysis_time = analysis_time
    # Insert results into database
    for bus_rev in business_reviews:
        kate_row = KateResult()
        kate_row.business = get_business_name(database, bus_rev.business_id)
        kate_row.star_average = bus_rev.get_star_avg()
        kate_row.sentiment_average = bus_rev.get_sentiment()
        kate_row.benchmark = benchmark
        kate_row.total_reviews = bus_rev.positive_count + bus_rev.negative_count
        tbl_kate.insert(kate_row)


def get_business_name(database, business_id):
    """Returns the name of a business given its ID"""
    if database == 'JanusGraph':
        return janus_graph.execute_query(
            'g.V().has("Business", "business_id", "{}").next().values("name")'.format(business_id))[0]
    elif database == "PostgreSQL":
        return postgres.execute_query('SELECT name FROM business WHERE id = \'{}\''.format(business_id))[0][0]


def update_business_reviews(database, recent_reviews, business_reviews):
    """Adds the review to object and classifies sentiment for each review"""
    start = perf_counter_ns()
    if database == 'JanusGraph':
        for review in recent_reviews:
            business_review = next((x for x in business_reviews if x.business_id == review['business_id']), None)
            if business_review is None:
                business_reviews.append(BusinessReview(
                    business_id=review["business_id"],
                    text=review["text"],
                    stars=review["stars"]))
            else:
                business_review.total_stars += review['stars']
                business_review.add_sentiment(review['text'])
    elif database == "PostgreSQL":
        for stars, text, business_id in recent_reviews:
            business_review = next((x for x in business_reviews if x.business_id == business_id), None)
            if business_review is None:
                business_reviews.append(BusinessReview(
                    business_id=business_id,
                    text=text,
                    stars=stars))
            else:
                business_review.total_stars += stars
                business_review.add_sentiment(text)
    return (perf_counter_ns() - start) / 1000000


def get_recent_reviews_for_user_near_kate(database, user_id):
    """The following query returns a given user's reviews of restaurants near Kate that they have rated over 3 stars"""
    result = ''
    start = perf_counter_ns()
    if database == "JanusGraph":
        result = janus_graph.execute_query(
            'g.V().has("User", "user_id", "{}").outE("REVIEWS").has("stars", gt(3)).order().by("date", desc)'
            '.as("stars", "text").inV().has("location", geoWithin(Geoshape.circle(35.15,-80.79, 5))).as("business_id")'
            '.select("stars").limit(10).select("stars", "text", "business_id")'
            '.by("stars").by("text").by("business_id")'
            .format(user_id)
        )
    elif database == "PostgreSQL":
        result = postgres.execute_query(
            'SELECT review.stars, review.text, review.business_id FROM review JOIN business '
            'ON review.business_id = business.id '
            'AND review.user_id = \'{}\' AND ST_DWithin(location, ST_MakePoint(-80.79, 35.15)::geography, 5000) '
            'AND review.stars > 3 ORDER BY review.date DESC LIMIT 10'.format(user_id)
        )

    time_elapsed = (perf_counter_ns() - start) / 1000000

    return result, time_elapsed


def get_users_who_like_same_restaurants_as_kate(database):
    """The following query returns all user ids who have reviewed restaurants that Kate rates over 3 stars."""
    result = ''
    start = perf_counter_ns()
    if database == "JanusGraph":
        result = janus_graph.execute_query(
            'g.V().has("User", "user_id", "{}").as("kate").outE("REVIEWS").has("stars", gt(3)).inV().in("REVIEWS")'
            '.where(neq("kate")).as("users").out("REVIEWS").out("IN_CATEGORY").has("name", "Restaurants")'
            '.select("users").dedup().values("user_id").fold()'.format(kate_id)
        )[0]
    elif database == "PostgreSQL":
        result = postgres.execute_query(
            'SELECT DISTINCT OtherReviews.user_id FROM users JOIN review KateReviews '
            'ON users.id = KateReviews.user_id AND users.id = \'{}\' AND KateReviews.stars > 3 JOIN business KateBus '
            'ON KateReviews.business_id = KateBus.id JOIN review OtherReviews '
            'ON OtherReviews.user_id != KateReviews.user_id AND OtherReviews.business_id = KateReviews.business_id '
            'JOIN bus_2_cat Bus2Cat ON OtherReviews.business_id = Bus2Cat.business_id '
            'JOIN category Categories ON Bus2Cat.category_id = Categories.id '
            'AND Categories.name = \'Restaurants\''.format(kate_id)
        )
        result = [i[0] for i in result]
    time_elapsed = (perf_counter_ns() - start) / 1000000

    return result, time_elapsed


class BusinessReview(object):

    def __init__(self, business_id, text, stars):
        self.business_id = business_id
        self.total_stars = stars
        self.positive_count = 0
        self.negative_count = 0
        if sentiment.classify(text) == "pos":
            self.positive_count = 1
        else:
            self.negative_count = 1

    def add_sentiment(self, text):
        if sentiment.classify(text) == "pos":
            self.positive_count += 1
        else:
            self.negative_count += 1

    def get_star_avg(self):
        return self.total_stars / (self.positive_count + self.negative_count)

    def get_sentiment(self):
        return self.positive_count / (self.positive_count + self.negative_count) * 100

    def __eq__(self, other):
        if isinstance(other, str):
            return self.business_id == other
        elif isinstance(other, BusinessReview):
            return other.__hash__() == self.__hash__()
        else:
            raise NotImplementedError(str(other))

    def __hash__(self):
        return hash(self.business_id)

    def __str__(self):
        return "{{'business_id': '{}', 'positive_count': {}, 'negative_count': {}, " \
               "'total_stars': {}}}" \
            .format(self.business_id, self.positive_count, self.negative_count, self.total_stars)
