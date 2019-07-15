import json


class Analysis(object):

    def __init__(self):
        self.analysis_id = None
        self.dataset = None
        self.name = None
        self.description = None

    @property
    def json(self):
        out = dict()
        out['id'] = self.analysis_id
        out['dataset'] = self.dataset.json
        out['name'] = self.name
        out['description'] = self.description

        return out

    def __str__(self):
        import json

        return json.dumps(self.json, default=str)


class Benchmark(object):

    def __init__(self):
        self.benchmark_id = None
        self.database = None
        self.dataset = None
        self.analysis = None
        self.date_executed = None
        self.title = None
        self.description = None
        self.query_time = None
        self.analysis_time = None

    @property
    def json(self):
        out = dict()
        out['id'] = self.benchmark_id
        out['database'] = self.database.json
        out['dataset'] = self.dataset.json
        out['analysis'] = self.analysis.json
        out['date_executed'] = self.date_executed
        out['title'] = self.title
        out['description'] = self.description
        out['query_time'] = self.query_time
        out['analysis_time'] = self.analysis_time

        return out

    def __str__(self):
        return json.dumps(self.json, default=str)


class Database(object):

    def __init__(self):
        self.database_id = None
        self.name = None
        self.description = None
        self.icon = None

    @property
    def json(self):
        out = dict()
        out['id'] = self.database_id
        out['name'] = self.name
        out['description'] = self.description
        out['icon'] = self.icon

        return out

    def __str__(self):
        import json

        return json.dumps(self.json, default=str)


class Dataset(object):

    def __init__(self):
        self.dataset_id = None
        self.name = None
        self.description = None
        self.icon = None

    @property
    def json(self):
        out = dict()
        out['id'] = self.dataset_id
        out['name'] = self.name
        out['description'] = self.description
        out['icon'] = self.icon

        return out

    def __str__(self):
        import json

        return json.dumps(self.json, default=str)


class Graph(object):

    def __init__(self):
        self.graph_id = None
        self.benchmark_id = None
        self.graphson = None


def model_encoder(o):
    """
    Encodes the given model object as a JSON string. This function should be passed
    in the 'default' parameter in json.dump().
    :param o: model to encode.
    :return: JSON format string representation of the model.
    """
    if isinstance(o, Analysis):
        pass
    elif isinstance(o, Benchmark):
        pass
    elif isinstance(o, Dataset):
        pass
    elif isinstance(o, Database):
        pass
    elif isinstance(o, Graph):
        pass


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
    benchmark.title = o['title']
    benchmark.description = o['description']
    benchmark.query_time = o['query_time']
    benchmark.analysis_time = o['analysis_time']
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
