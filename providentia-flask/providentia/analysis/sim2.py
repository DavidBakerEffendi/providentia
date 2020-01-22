import logging
from datetime import datetime
from time import perf_counter_ns

from providentia.db import janus_graph, postgres, tigergraph
from providentia.models import Benchmark, Sim2
from providentia.repository import tbl_sim2

analysis_id = "34a6d0e2-ca77-4615-a873-9a0d0b92559b"


def run(benchmark: Benchmark):
    database = benchmark.database.name
    logging.debug("Starting simulation analysis 2 using %s", database)
    # initialize timers
    benchmark.date_executed = datetime.utcnow()
    # Run query
    start = perf_counter_ns()
    result = query_database(database)
    q1_total_time = (perf_counter_ns() - start) / 1000000
    # Analyse results
    analysis_time = 0
    # Add time
    total_time = q1_total_time + analysis_time
    logging.info('\n===== Completed Simulation Analysis 2 =====\n'
                 'Database:\t\t%s\n'
                 'Total query time:\t%.2f ms\n'
                 'Time analysing:\t\t%.2f ms\n'
                 '------------------------------------\n'
                 'Total time:\t\t%.2f ms\n',
                 database, q1_total_time, analysis_time, total_time)
    # Update benchmark object values
    benchmark.query_time = q1_total_time
    benchmark.analysis_time = analysis_time
    # Save result
    sim = Sim2()
    sim.benchmark = benchmark
    sim.p1 = result[1]
    sim.p2 = result[2]
    sim.p3 = result[3]
    tbl_sim2.insert(sim)


def query_database(database):
    result = []
    if database == 'JanusGraph':
        result = janus_graph.execute_query(
            'g.V().hasLabel("Resource").has("resource_id", "2")'
            '.in("RESPONSE_RESOURCE").hasLabel("Response")'
            '.has("destination", geoWithin(Geoshape.circle(63.67, 19.11, 0.5)))'
            '.as("RES")'
            '.out("RESPONSE_SCENE").as("SCN")'
            '.select("RES")'
            '.groupCount().by(out("RESPONSE_PRIORITY").values("priority_id"))')
        result = result[0]
    elif database == "PostgreSQL":
        result = postgres.execute_query(
            'SELECT count(*) as responses_by_prio '
            'FROM resource RSC '
            'JOIN response RES ON RSC.response_id = RES.id '
            'JOIN on_scene SCN ON SCN.response_id = RES.id '
            'JOIN priority PRI ON PRI.response_id = RES.id '
            'WHERE RSC.id = 2 '
            'AND ST_DWithin(RES.destination, ST_MakePoint(19.11, 63.67)::geography, 500) '
            'GROUP BY PRI.id;', 'phosim')
        result = {
            1: int(result[0][0]),
            2: int(result[1][0]),
            3: int(result[2][0])
        }
    elif database == "TigerGraph":
        req = tigergraph.execute_query('postSim2')
        if req is not None:
            result = req[0]['@@group']
            result = {
                int(result[0]['prio']): int(result[0]['total']),
                int(result[1]['prio']): int(result[1]['total']),
                int(result[2]['prio']): int(result[2]['total'])
            }
    return result
