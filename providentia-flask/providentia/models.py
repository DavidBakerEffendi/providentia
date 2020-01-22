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


class Query(object):

    def __init__(self):
        self.query_id = None
        self.analysis = None
        self.database = None
        self.query = None
        self.language = None


class KateResult(object):

    def __init__(self):
        self.id = None
        self.benchmark = None
        self.business = None
        self.sentiment_average = None
        self.star_average = None
        self.total_reviews = None


class ReviewTrendResult(object):

    def __init__(self):
        self.id = None
        self.benchmark = None
        self.stars = None
        self.length = None
        self.cool = None
        self.funny = None
        self.useful = None
        self.sentiment = None


class CitySentimentResult(object):

    def __init__(self):
        self.id = None
        self.benchmark = None
        self.stars = None
        self.sentiment = None


class Sim1(object):

    def __init__(self):
        self.id = None
        self.benchmark = None
        self.avg_ttas = None
        self.avg_tth = None


class Sim2(object):

    def __init__(self):
        self.id = None
        self.benchmark = None
        self.p1 = None
        self.p2 = None
        self.p3 = None


class Sim3(object):

    def __init__(self):
        self.id = None
        self.benchmark = None
        self.no_responses = None


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
    elif isinstance(o, Query):
        return o.__dict__
    elif isinstance(o, KateResult):
        return o.__dict__
    elif isinstance(o, ReviewTrendResult):
        return o.__dict__
    elif isinstance(o, CitySentimentResult):
        return o.__dict__
    elif isinstance(o, Sim1):
        return o.__dict__
    elif isinstance(o, Sim2):
        return o.__dict__
    elif isinstance(o, Sim3):
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
    from providentia.repository import tbl_datasets
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
    from providentia.repository import tbl_analysis, tbl_databases, tbl_datasets
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
    from providentia.repository import tbl_cpu_logs
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


def query_decoder(o: dict):
    """
    Decodes the given JSON string and returns its respective model. This function
    should be passed into the 'object_hook' parameter in json.load().
    :param o: JSON string as a dict.
    :return: the respective model object.
    """
    from providentia.repository import tbl_analysis, tbl_databases
    query = Query()
    query.query_id = o['id']
    query.analysis = tbl_analysis.find(o['analysis_id'])
    query.database = tbl_databases.find(o['database_id'])
    query.query = o['query']
    query.language = o['language']
    return query


def kate_decoder(o: dict):
    """
    Decodes the given JSON string and returns its respective model. This function
    should be passed into the 'object_hook' parameter in json.load().
    :param o: JSON string as a dict.
    :return: the respective model object.
    """
    from providentia.repository import tbl_benchmark
    kate = KateResult()
    kate.id = o['id']
    kate.business = o['business']
    kate.benchmark = tbl_benchmark.find(o['benchmark_id'])
    kate.sentiment_average = o['sentiment_average']
    kate.star_average = o['star_average']
    kate.total_reviews = o['total_reviews']
    return kate


def review_trend_decoder(o: dict):
    """
    Decodes the given JSON string and returns its respective model. This function
    should be passed into the 'object_hook' parameter in json.load().
    :param o: JSON string as a dict.
    :return: the respective model object.
    """
    from providentia.repository import tbl_benchmark
    review_trend = ReviewTrendResult()
    review_trend.id = o['id']
    review_trend.benchmark = tbl_benchmark.find(o['benchmark_id'])
    review_trend.stars = o['stars']
    review_trend.length = o['length']
    review_trend.cool = o['cool']
    review_trend.funny = o['funny']
    review_trend.useful = o['useful']
    review_trend.sentiment = o['sentiment']
    return review_trend


def city_sentiment_decoder(o: dict):
    """
    Decodes the given JSON string and returns its respective model. This function
    should be passed into the 'object_hook' parameter in json.load().
    :param o: JSON string as a dict.
    :return: the respective model object.
    """
    from providentia.repository import tbl_benchmark
    city_sentiment = CitySentimentResult()
    city_sentiment.id = o['id']
    city_sentiment.benchmark = tbl_benchmark.find(o['benchmark_id'])
    city_sentiment.stars = o['stars']
    city_sentiment.sentiment = o['sentiment']
    return city_sentiment

def sim1_decoder(o: dict):
    """
    Decodes the given JSON string and returns its respective model. This function
    should be passed into the 'object_hook' parameter in json.load().
    :param o: JSON string as a dict.
    :return: the respective model object.
    """
    from providentia.repository import tbl_benchmark
    sim = Sim1()
    sim.id = o['id']
    sim.benchmark = tbl_benchmark.find(o['benchmark_id'])
    sim.avg_ttas = o['avg_ttas']
    sim.avg_tth = o['avg_tth']
    return sim

def sim2_decoder(o: dict):
    """
    Decodes the given JSON string and returns its respective model. This function
    should be passed into the 'object_hook' parameter in json.load().
    :param o: JSON string as a dict.
    :return: the respective model object.
    """
    from providentia.repository import tbl_benchmark
    sim = Sim2()
    sim.id = o['id']
    sim.benchmark = tbl_benchmark.find(o['benchmark_id'])
    sim.p1 = o['p1']
    sim.p2 = o['p2']
    sim.p3 = o['p3']
    return sim

def sim3_decoder(o: dict):
    """
    Decodes the given JSON string and returns its respective model. This function
    should be passed into the 'object_hook' parameter in json.load().
    :param o: JSON string as a dict.
    :return: the respective model object.
    """
    from providentia.repository import tbl_benchmark
    sim = Sim3()
    sim.id = o['id']
    sim.benchmark = tbl_benchmark.find(o['benchmark_id'])
    sim.no_responses = o['no_responses']
    return sim

def new_benchmark_decoder(o: dict):
    """
    Decodes the given JSON string and returns its respective model. This function
    should be passed into the 'object_hook' parameter in json.load().
    :param o: JSON string as a dict.
    :return: the respective model object.
    """
    from providentia.repository import tbl_databases
    from providentia.repository import tbl_datasets
    from providentia.repository import tbl_analysis
    benchmark = Benchmark()

    database = tbl_databases.find_name(o['database'])
    dataset = tbl_datasets.find_name(o['dataset'])
    analysis = tbl_analysis.find_name(o['analysis'])

    if database is None:
        raise KeyError('Database "{}" is not supported!'.format(o['database']))
    if dataset is None:
        raise KeyError('Dataset "{}" is not supported!'.format(o['dataset']))
    if analysis is None:
        raise KeyError('Analysis "{}" is not supported!'.format(o['analysis']))

    benchmark.database = database.database_id
    benchmark.dataset = dataset.dataset_id
    benchmark.analysis = analysis.analysis_id
    benchmark.query_time = 0
    benchmark.analysis_time = 0

    return benchmark
