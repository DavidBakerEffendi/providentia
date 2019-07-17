import logging
from datetime import datetime
from threading import Lock

import psutil

from providentia.models import ServerLog, CPULog
from providentia.repository.this import tbl_benchmark, tbl_server_logs, tbl_cpu_logs

lock = Lock()
analysis_running = False


def execute_waiting():
    """
    Looks for jobs not currently being executed in the pipeline and processes one if the pipeline is
    currently empty.
    """
    global analysis_running
    global lock

    if analysis_running is True:
        return

    with lock:
        analysis_running = True
        from providentia import app

        with app.app_context():
            logging.debug('Looking for benchmark jobs to execute...')
            # TODO: Need to look for jobs which were starts but unfinished first
            unstarted_jobs = tbl_benchmark.get_unstarted_jobs()
            if unstarted_jobs is None:
                logging.debug('No jobs available, going back to sleep.')
            else:
                logging.debug('Found unstarted jobs!')

        analysis_running = False


def log_server_state():
    from providentia import app

    # Create system log
    system_log = ServerLog()
    system_log.memory_perc = dict(psutil.virtual_memory()._asdict())['percent']
    system_log.captured_at = datetime.now()

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
