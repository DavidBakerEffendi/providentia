import logging
from config import default_config

config = default_config()
logging.basicConfig(level=config.LOGGING_LEVEL)


def deserialize(obj):
    if type(obj) is dict:
        dataset = Dataset()
        dataset.dataset_id = obj['id']
        dataset.name = obj['name']
        dataset.description = obj['description']
        dataset.icon = obj['icon']
        return dataset
    elif type(obj) is str:
        pass
    else:
        raise NotImplementedError("Cannot deserialize data that is not dict or JSON format.")


class Dataset(object):

    def __init__(self):
        self.__dataset_id = None
        self.__name = None
        self.__description = None
        self.__icon = None

    @property
    def dataset_id(self):
        return self.__dataset_id

    @dataset_id.setter
    def dataset_id(self, __dataset_id):
        self.__dataset_id = __dataset_id

    @property
    def name(self):
        return self.__name

    @name.setter
    def name(self, __name):
        self.__name = __name

    @property
    def description(self):
        return self.__description

    @description.setter
    def description(self, __description):
        self.__description = __description

    @property
    def icon(self):
        return self.__icon

    @icon.setter
    def icon(self, __icon):
        self.__icon = __icon

    def to_json(self):
        json = dict()
        json['id'] = self.__dataset_id
        json['name'] = self.__name
        json['description'] = self.__description
        json['icon'] = self.__icon

        return json

    def __str__(self):
        import json

        return json.dumps(self.to_json(), default=str)
