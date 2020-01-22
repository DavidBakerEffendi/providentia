import logging
from datetime import datetime
from time import perf_counter_ns

from providentia.db import janus_graph, postgres, tigergraph
from providentia.models import Benchmark, Sim3
from providentia.repository import tbl_sim3

analysis_id = "2d8ca3c7-ab16-4567-a821-1d480ce19bfa"


def run(benchmark: Benchmark):
    database = benchmark.database.name
    logging.debug("Starting simulation analysis 3 using %s", database)
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
    logging.info('\n===== Completed Simulation Analysis 3 =====\n'
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
    sim = Sim3()
    sim.benchmark = benchmark
    sim.no_responses = result
    tbl_sim3.insert(sim)


def query_database(database):
    result = {}
    if database == 'JanusGraph':
        result = janus_graph.execute_query(
            'g.V().hasLabel("Transfer").as("tth")'
            '.in("RESPONSE_TRANSFER").as("ttas", "osd")'
            '.where(math("ttas + osd + tth")'
            '.by(values("time_to_ambulance_starts"))'
            '.by(values("on_scene_duration"))'
            '.by(values("travel_time_hospital"))'
            '.is(gt(15 * 60)))'
            '.count()')
        result = result[0]
    elif database == "PostgreSQL":
        result = postgres.execute_query(
            'SELECT count(*) '
            'FROM transfer TNS '
            'JOIN response RES ON TNS.response_id = RES.id '
            'WHERE RES.time_to_ambulance_starts '
            '+ RES.on_scene_duration '
            '+ TNS.travel_time_hospital > 15 * 60;', 'phosim')
        result = result[0][0]
    elif database == "TigerGraph":
        req = tigergraph.execute_query('postSim3')
        if req is not None:
            result = req[0]['@@totalResponses']
    return result
