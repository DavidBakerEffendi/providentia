import logging
from config import default_config

config = default_config()
logging.basicConfig(level=config.LOGGING_LEVEL)


def deserialize(obj):

    if type(obj) is dict:
        database = Database()
        database.database_id = obj['id']
        database.name = obj['name']
        database.description = obj['description']
        database.icon = obj['icon']
        return database
    elif type(obj) is str:
        pass
    else:
        raise NotImplementedError("Cannot deserialize data that is not dict or JSON format.")


class Database(object):

    def __init__(self):
        self.__database_id = None
        self.__name = None
        self.__description = None
        self.__icon = None

    @property
    def database_id(self):
        return self.__database_id

    @database_id.setter
    def database_id(self, __database_id):
        self.__database_id = __database_id

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
        json['id'] = self.__database_id
        json['name'] = self.__name
        json['description'] = self.__description
        json['icon'] = self.__icon

        return json

    def __str__(self):
        import json

        return json.dumps(self.to_json(), default=str)
