import logging
from datetime import datetime
from time import perf_counter_ns

from providentia.classifier import sentiment
from providentia.db import janus_graph, postgres
from providentia.models import Benchmark, KateResult
from providentia.repository import tbl_kate

analysis_id = "b540a4dd-f010-423b-9644-aef4e9b754a9"


def run(benchmark: Benchmark):
    database = benchmark.database.name
    logging.debug("Starting review trend analysis using %s", database)
