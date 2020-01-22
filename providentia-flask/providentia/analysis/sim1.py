import logging
from datetime import datetime
from time import perf_counter_ns

from providentia.db import janus_graph, postgres, tigergraph
from providentia.models import Benchmark, Sim1
from providentia.repository import tbl_sim1

analysis_id = "899760bd-417e-431c-bac1-d5e4a8e16462"


def run(benchmark: Benchmark):
    database = benchmark.database.name
    logging.debug("Starting simulation analysis 1 using %s", database)
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
    logging.info('\n===== Completed Simulation Analysis 1 =====\n'
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
    sim = Sim1()
    sim.benchmark = benchmark
    sim.avg_ttas = result['avg_ttas']
    sim.avg_tth = result['avg_tth']
    tbl_sim1.insert(sim)


def query_database(database):
    result = {}
    if database == 'JanusGraph':
        result = janus_graph.execute_query(
            'g.V().hasLabel("Priority").has("priority_id", "1")'
            '.in("RESPONSE_PRIORITY").hasLabel("Response")'
            '.has("destination", geoWithin(Geoshape.circle(63.67, 19.11, 0.5)))'
            '.fold().aggregate("avg_ttas")'
            '.by(unfold().values("time_to_ambulance_starts").mean())'
            '.unfold().out("RESPONSE_TRANSFER")'
            '.fold().aggregate("avg_tth")'
            '.by(unfold().values("travel_time_hospital").mean())'
            '.select("avg_ttas", "avg_tth")')
        result = {
            'avg_ttas': float(result[0]['avg_ttas'][0]),
            'avg_tth': float(result[0]['avg_tth'][0]),
        }
    elif database == "PostgreSQL":
        result = postgres.execute_query(
            'SELECT avg(RES.time_to_ambulance_starts) as avg_ttas, '
            'avg(TNS.travel_time_hospital) as avg_tth '
            'FROM priority PRI '
            'JOIN response RES ON RES.id = PRI.response_id '
            'JOIN transfer TNS ON RES.id = TNS.response_id '
            'WHERE PRI.id = 1 '
            'AND ST_DWithin(RES.destination, ST_MakePoint(19.11, 63.67)::geography, 500);', 'phosim')
        result = {
            'avg_ttas': float(result[0][0]),
            'avg_tth': float(result[0][1]),
        }
    elif database == "TigerGraph":
        req = tigergraph.execute_query('postSim1')
        if req is not None:
            result = {
                'avg_ttas': req[0]['@@avgTtas'],
                'avg_tth': req[1]['@@avgTth'],
            }
    return result
