#
# microtome

from abc import ABCMeta, abstractmethod

import core.manager

def createCtx():
    return core.manager.MicrotomeMgr()

class MicrotomeCtx(object):
    __metaclass__ = ABCMeta

    @abstractmethod
    def register_page_classes(self, classes):
        pass

    @abstractmethod
    def register_data_marshaller(self, marshaller):
        pass

    @abstractmethod
    def load(self, library, data):
        pass

    @abstractmethod
    def write(self, item, writer):
        pass

    @abstractmethod
    def clone(self, item):
        return None


if __name__ == "__main__":
    createCtx()