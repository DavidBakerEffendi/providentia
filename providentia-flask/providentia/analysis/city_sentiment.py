import logging
from datetime import datetime
from time import perf_counter_ns

from providentia.db import janus_graph, postgres, tigergraph
from providentia.models import Benchmark

analysis_id = "05c2c642-32c0-4e6a-a0e5-c53028035fc8"
julie_id = "7weuSPSSqYLUFga6IYP4pg"

cities = {
    'Phoenix': (33.54, -112.17),
    'Las Vegas': (36.16, -115.14),
    'Toronto': (43.72, -79.38)
}


def run(benchmark: Benchmark):
    database = benchmark.database.name
    logging.debug("Starting review trend analysis using %s", database)
    # initialize timers
    benchmark.date_executed = datetime.utcnow()
    # Run query
    start = perf_counter_ns()
    reviews = get_reviews_per_city(database)
    q1_total_time = (perf_counter_ns() - start) / 1000000
    # Analyse results
    start = perf_counter_ns()
    # TODO
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


def get_reviews_per_city(database):
    reviews = {}
    for c in cities.keys():
        lat, lon = cities[c]
        if database == 'JanusGraph':
            result = janus_graph.execute_query(
                'g.V().has("User", "user_id", "{}").out("FRIENDS").outE("REVIEWS").filter{'
                'it.get().value("date").atZone(ZoneId.of("-07:00")).toLocalDate().getMonthValue() >= 10 &&'
                'it.get().value("date").atZone(ZoneId.of("-07:00")).toLocalDate().getMonthValue() <= 12}.as("text")'
                '.inV().has("location", geoWithin(Geoshape.circle({}, {}, 30))).select("text").by("text")'
                    .format(julie_id, lat, lon))
            reviews[c] = result
        elif database == "PostgreSQL":
            result = postgres.execute_query(
                'SELECT R.text FROM review R '
                'JOIN business B ON R.business_Id = B.id '
                'JOIN friends F ON R.user_id = F.friend_id '
                'AND F.user_id = {} '
                'AND ST_DWithin(B.location, ST_MakePoint({}, {}})::geography, 30000)'
                'AND (date_part("month", R.date) >= 10 AND date_part("month", R.date) <= 12)'
                    .format(julie_id, lon, lat))
            reviews[c] = result
        elif database == "TigerGraph":
            req = tigergraph.execute_query('getFriendReviewsInArea?p={}&lat={}&lon={}'.format(julie_id, lat, lon))
            if req is not None:
                result = req[0]['@@reviews']
            else:
                result = []
            reviews[c] = result
