#
# microtome

import xml.etree.ElementTree as ElementTree

from microtome.core.reader import ReadableObject
from microtome.error import LoadError

def load_xml(ctx, lib, *filenames):
    ctx.load(lib, readers_from_files(*filenames))


def readers_from_xml_strings(*xml_strings):
    return [XmlObject(root) for root in
            [ElementTree.fromstring(xml_string) for xml_string in xml_strings]]


def readers_from_files(*filenames):
    return [XmlObject(tree.getroot()) for tree in
            [ElementTree.parse(filename) for filename in filenames]]


class XmlObject(ReadableObject):
    def __init__(self, xml):
        self._xml = xml

    @property
    def name(self):
        return self._xml.tag

    @property
    def debug_description(self):
        return ElementTree.tostring(self._xml).split("\n")[0]

    @property
    def children(self):
        return [XmlObject(child) for child in list(self._xml)]

    def has_value(self, name):
        return name in self._xml.attrib

    def get_string(self, name):
        out = self._xml.get(name, None)
        if out is None:
            raise LoadError(self, "missing required attribute [name=%s]" % name)
        return out

    def get_bool(self, name):
        attr = self.get_string(name).lower()
        if attr == "true" or attr == "yes":
            return True
        elif attr == "false" or attr == "no":
            return False
        else:
            raise RuntimeError("'%s' is not a boolean" % attr)

    def get_int(self, name):
        return int(self.get_string(name))

    def get_float(self, name):
        return float(self.get_string(name))


if __name__ == "__main__":
    test_xml = r'<root><foo bar="1" baz="qwert"/></root>'
    reader = readers_from_xml_strings(test_xml)[0]
    print reader.name
