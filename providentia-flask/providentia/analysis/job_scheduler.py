import logging
from threading import Lock
from config import default_config
import providentia.repository.benchmark_repository as bm_table

config = default_config()
logging.basicConfig(level=config.LOGGING_LEVEL)
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
            unstarted_jobs = bm_table.get_unstarted_jobs()
            if unstarted_jobs is None:
                logging.debug('No jobs available, going back to sleep.')
            else:
                logging.debug('Found unstarted jobs!')

        analysis_running = False
