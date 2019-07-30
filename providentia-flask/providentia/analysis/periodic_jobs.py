import logging
import random
from datetime import datetime

import psutil

from providentia.models import ServerLog, CPULog, Benchmark
from providentia.repository import tbl_benchmark, tbl_server_logs, tbl_cpu_logs


def execute_waiting():
    """
    Looks for jobs not currently being executed in the pipeline and processes one if the pipeline is
    currently empty.
    """
    from providentia import app
    from providentia.classifier import sentiment

    # only run analysis if classifier is ready
    if sentiment.classify("test") is None:
        logging.debug('Classifier not ready, going back to sleep.')
        return

    # look to process a job
    with app.app_context():
        if tbl_benchmark.is_job_being_processed():
            logging.debug('A job is being processed, going back to sleep')
        else:
            logging.debug('No job being processed, looking for jobs to execute')
            unstarted_jobs = tbl_benchmark.get_unstarted_jobs()

            if unstarted_jobs is None:
                logging.debug('No jobs available, going back to sleep.')
            else:
                logging.debug('Found unstarted jobs!')
                random.shuffle(unstarted_jobs)
                start_job(unstarted_jobs.pop())


def start_job(benchmark: Benchmark):
    """Updates the status of benchmarks and decides which analysis to run"""
    from providentia.analysis import kate, review_trends

    tbl_benchmark.set_as(benchmark.benchmark_id, 'PROCESSING')
    if benchmark.analysis.analysis_id == kate.analysis_id:
        kate.run(benchmark)
    elif benchmark.analysis.analysis_id == review_trends.analysis_id:
        review_trends.run(benchmark)
        print('review trends lol')

    benchmark.status = 'COMPLETE'
    tbl_benchmark.update(benchmark)


def log_server_state():
    from providentia import app

    # Create system log
    system_log = ServerLog()
    system_log.memory_perc = dict(psutil.virtual_memory()._asdict())['percent']
    system_log.captured_at = datetime.utcnow()

    with app.app_context():
        tbl_server_logs.insert_log(system_log)

        # Link CPU logs to system log
        count = 0
        for perc in psutil.cpu_percent(percpu=True):
            cpu_log = CPULog()
            cpu_log.cpu_perc = perc
            cpu_log.system_log_id = system_log.log_id
            cpu_log.core_id = count
            tbl_cpu_logs.insert_log(cpu_log)
            count += 1
