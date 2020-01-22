import logging
from datetime import datetime
from time import perf_counter_ns

from nltk.tokenize import RegexpTokenizer

from providentia.classifier import sentiment
from providentia.db import janus_graph, postgres, tigergraph
from providentia.models import Benchmark, ReviewTrendResult
from providentia.repository.analysis_tables import tbl_review_trends

analysis_id = "b540a4dd-f010-423b-9644-aef4e9b754a9"


def run(benchmark: Benchmark):
    database = benchmark.database.name
    logging.debug("Starting review trend analysis using %s", database)
    # initialize timers
    benchmark.date_executed = datetime.utcnow()
    # Run query
    start = perf_counter_ns()
    reviews = get_reviews_from_phoenix_2018(database)
    q1_total_time = (perf_counter_ns() - start) / 1000000
    # Analyse results
    start = perf_counter_ns()
    star_map = aggregate_reviews(database, reviews)
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
    # Normalize data for graph and insert into table
    normalize_data(benchmark, star_map)
    # Update benchmark object values
    benchmark.query_time = q1_total_time
    benchmark.analysis_time = analysis_time


def normalize_data(benchmark, star_map):
    """Normalizes data between 0.0 and 1.0 using min-max scaling."""

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

    # Extract lists by category
    length_l, cool_l, funny_l, useful_l, sentiment_l, normalized_data = [], [], [], [], [], []

    for star, review in star_map.items():
        length_l.append(review.get_length_avg())
        cool_l.append(review.get_cool_avg())
        funny_l.append(review.get_funny_avg())
        useful_l.append(review.get_useful_avg())
        sentiment_l.append(review.get_sentiment())
    # Scale each category
    length_l, cool_l, funny_l, useful_l, sentiment_l = scale_column(length_l), scale_column(cool_l), scale_column(
        funny_l), scale_column(useful_l), scale_column(sentiment_l)
    # Zip together and add data to database
    for star, length, cool, funny, useful, sentiment in \
            zip(star_map.keys(), length_l, cool_l, funny_l, useful_l, sentiment_l):
        norm = ReviewTrendResult()
        norm.benchmark = benchmark
        norm.stars = star
        norm.length = length
        norm.cool = cool
        norm.funny = funny
        norm.useful = useful
        norm.sentiment = sentiment
        tbl_review_trends.insert(norm)


def get_reviews_from_phoenix_2018(database):
    if database == 'JanusGraph':
        return janus_graph.execute_query(
            'g.V().has("Business", "location", geoWithin(Geoshape.circle(33.45,-112.56, 50))).inE("REVIEWS")'
            '.has("date", between(Instant.parse("2018-01-01T00:00:00.00Z"), Instant.parse("2018-12-31T23:59:59.99Z")))'
            '.valueMap()')
    elif database == "PostgreSQL":
        return postgres.execute_query(
            'SELECT text, review.stars, cool, funny, useful FROM business JOIN review ON business.id = '
            'review.business_id AND ST_DWithin(location, ST_MakePoint(-112.56, 33.45)::geography, 50000) '
            'AND date_part(\'year\', date) = 2018', 'yelp')
    elif database == "TigerGraph":
        req = tigergraph.execute_query('getReviewsFromPhoenix2018')
        if req is not None:
            return req[0]['@@reviewList']
        else:
            return []


def aggregate_reviews(database, reviews):
    star_map = {}

    if database == 'JanusGraph':
        # JanusGraph data as dict
        for r in reviews:
            if r['stars'] not in star_map.keys():
                pr = PhoenixReview()
                star_map[r['stars']] = pr
            else:
                pr = star_map[r['stars']]
            pr.add_review(r['text'], r['cool'], r['funny'], r['useful'])
    elif database == 'PostgreSQL':
        # PostgreSQL data as tuple
        for r in reviews:
            if r[1] not in star_map.keys():
                pr = PhoenixReview()
                star_map[float(r[1])] = pr
            else:
                pr = star_map[float(r[1])]
            pr.add_review(r[0], r[2], r[3], r[4])
    elif database == 'TigerGraph':
        # TigerGraph data as dict
        for r in reviews:
            if r['stars'] not in star_map.keys():
                pr = PhoenixReview()
                star_map[r['stars']] = pr
            else:
                pr = star_map[r['stars']]
            pr.add_review(r['text'], r['cool'], r['funny'], r['useful'])

    return star_map


class PhoenixReview(object):

    def __init__(self):
        self.length = 0
        self.cool = 0
        self.funny = 0
        self.useful = 0
        self.review_count = 0
        self.positive_count = 0
        self.negative_count = 0

    def add_review(self, text, cool, funny, useful):
        self.review_count += 1
        # Count number of words in the review without punctuation
        tokenizer = RegexpTokenizer(r'\w+')
        tokens = tokenizer.tokenize(text)
        self.length += len(tokens)
        self.cool += cool
        self.funny += funny
        self.useful += useful
        if sentiment.classify(text) == "pos":
            self.positive_count += 1
        else:
            self.negative_count += 1

    def get_length_avg(self):
        if self.review_count < 1:
            return 0
        return self.length / self.review_count * 100

    def get_cool_avg(self):
        if self.review_count < 1:
            return 0
        return self.cool / self.review_count * 100

    def get_funny_avg(self):
        if self.review_count < 1:
            return 0
        return self.funny / self.review_count * 100

    def get_useful_avg(self):
        if self.review_count < 1:
            return 0
        return self.useful / self.review_count * 100

    def get_sentiment(self):
        return self.positive_count / (self.positive_count + self.negative_count) * 100

    def __str__(self):
        return "{{'length': '{}', 'cool': {}, 'funny': {}, " \
               "'useful': {}, 'sentiment':{}}}" \
            .format(self.get_length_avg(), self.get_cool_avg(), self.get_funny_avg(), self.get_useful_avg(),
                    self.get_sentiment())
