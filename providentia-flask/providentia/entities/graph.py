
class Graph(object):

    from providentia.entities.result import Result

    def __init__(self):
        self.__graph_id = None
        self.__result = None
        self.__graphson = None

    @property
    def graph_id(self):
        return self.__graph_id

    @graph_id.setter
    def graph_id(self, __graph_id):
        self.__graph_id = __graph_id

    @property
    def result(self):
        return self.__result

    @result.setter
    def result(self, __result: Result):
        self.__result = __result

    @property
    def graphson(self):
        return self.__graphson

    @graphson.setter
    def graphSON(self, __graphson):
        self.__graphson = __graphson
