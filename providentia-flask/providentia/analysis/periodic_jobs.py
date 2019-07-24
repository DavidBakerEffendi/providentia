import logging
from datetime import datetime

import psutil

from providentia.models import ServerLog, CPULog
from providentia.repository.this import tbl_benchmark, tbl_server_logs, tbl_cpu_logs


def execute_waiting():
    """
    Looks for jobs not currently being executed in the pipeline and processes one if the pipeline is
    currently empty.
    """
    from providentia import app

    with app.app_context():
        if tbl_benchmark.is_job_being_processed():
            logging.debug('A job is being processed, going back to sleep')
        else:
            logging.debug('No job being processed, looking for jobs to execute')
            # TODO: Need to look for jobs which were starts but unfinished first
            unstarted_jobs = tbl_benchmark.get_unstarted_jobs()
            if unstarted_jobs is None:
                logging.debug('No jobs available, going back to sleep.')
            else:
                logging.debug('Found unstarted jobs!')


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
