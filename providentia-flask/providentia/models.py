class Analysis(object):

    def __init__(self):
        self.analysis_id = None
        self.dataset = None
        self.name = None
        self.description = None


class Benchmark(object):

    def __init__(self):
        self.benchmark_id = None
        self.database = None
        self.dataset = None
        self.analysis = None
        self.date_executed = None
        self.query_time = None
        self.analysis_time = None
        self.status = 'WAITING'


class Database(object):

    def __init__(self):
        self.database_id = None
        self.name = None
        self.description = None
        self.icon = None
        self.status = 'DOWN'


class Dataset(object):

    def __init__(self):
        self.dataset_id = None
        self.name = None
        self.description = None
        self.icon = None


class Graph(object):

    def __init__(self):
        self.graph_id = None
        self.benchmark_id = None
        self.graphson = None


class ServerLog(object):

    def __init__(self):
        self.log_id = None
        self.captured_at = None
        self.cpu_logs = None
        self.memory_perc = None


class CPULog(object):

    def __init__(self):
        self.log_id = None
        self.core_id = None
        self.system_log_id = None
        self.cpu_perc = None


def model_encoder(o):
    """
    Encodes the given model object as a JSON string. This function should be passed
    in the 'default' parameter in json.dump().
    :param o: model to encode.
    :return: JSON format string representation of the model.
    """
    if isinstance(o, Analysis):
        return o.__dict__
    elif isinstance(o, Benchmark):
        return o.__dict__
    elif isinstance(o, Dataset):
        return o.__dict__
    elif isinstance(o, Database):
        return o.__dict__
    elif isinstance(o, Graph):
        return o.__dict__
    elif isinstance(o, ServerLog):
        return o.__dict__
    elif isinstance(o, CPULog):
        return o.__dict__
    else:
        return str(o)


def analysis_decoder(o: dict):
    """
    Decodes the given JSON string and returns its respective model. This function
    should be passed into the 'object_hook' parameter in json.load().
    :param o: JSON string as a dict.
    :return: the respective model object.
    """
    from providentia.repository.this import tbl_datasets
    analysis = Analysis()
    analysis.analysis_id = o['id']
    analysis.dataset = tbl_datasets.find(o['dataset_id'])
    analysis.name = o['name']
    analysis.description = o['description']
    return analysis


def benchmark_decoder(o: dict):
    """
    Decodes the given JSON string and returns its respective model. This function
    should be passed into the 'object_hook' parameter in json.load().
    :param o: JSON string as a dict.
    :return: the respective model object.
    """
    from providentia.repository.this import tbl_analysis, tbl_databases, tbl_datasets
    benchmark = Benchmark()
    benchmark.benchmark_id = o['id']
    benchmark.database = tbl_databases.find(o['database_id'])
    benchmark.dataset = tbl_datasets.find(o['dataset_id'])
    benchmark.analysis = tbl_analysis.find(o['analysis_id'])
    benchmark.date_executed = o['date_executed']
    benchmark.query_time = o['query_time']
    benchmark.analysis_time = o['analysis_time']
    benchmark.status = o['status']
    return benchmark


def database_decoder(o: dict):
    """
    Decodes the given JSON string and returns its respective model. This function
    should be passed into the 'object_hook' parameter in json.load().
    :param o: JSON string as a dict.
    :return: the respective model object.
    """
    database = Database()
    database.database_id = o['id']
    database.name = o['name']
    database.description = o['description']
    database.icon = o['icon']
    database.status = o['status']
    return database


def dataset_decoder(o: dict):
    """
    Decodes the given JSON string and returns its respective model. This function
    should be passed into the 'object_hook' parameter in json.load().
    :param o: JSON string as a dict.
    :return: the respective model object.
    """
    dataset = Dataset()
    dataset.dataset_id = o['id']
    dataset.name = o['name']
    dataset.description = o['description']
    dataset.icon = o['icon']
    return dataset


def graph_decoder(o: dict):
    """
    Decodes the given JSON string and returns its respective model. This function
    should be passed into the 'object_hook' parameter in json.load().
    :param o: JSON string as a dict.
    :return: the respective model object.
    """
    pass


def server_log_decoder(o: dict):
    """
    Decodes the given JSON string and returns its respective model. This function
    should be passed into the 'object_hook' parameter in json.load().
    :param o: JSON string as a dict.
    :return: the respective model object.
    """
    from providentia.repository.this import tbl_cpu_logs
    log = ServerLog()
    log.log_id = o['id']
    log.captured_at = o['captured_at']
    log.cpu_logs = tbl_cpu_logs.query_log(log.log_id)
    log.memory_perc = o['memory_perc']
    return log


def cpu_log_decoder(o: dict):
    """
    Decodes the given JSON string and returns its respective model. This function
    should be passed into the 'object_hook' parameter in json.load().
    :param o: JSON string as a dict.
    :return: the respective model object.
    """
    log = CPULog()
    log.log_id = o['id']
    log.system_log_id = o['system_log_id']
    log.core_id = o['core_id']
    log.cpu_perc = o['cpu_perc']
    return log
