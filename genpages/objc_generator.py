#
# microtome - Tim Conkling, 2012

import pystache
import spec as s

BASE_PAGE_CLASS = "MTMutablePage"
BOOL_TYPENAME = "BOOL"
STRING_TYPENAME = "NSString"

def generate (page_spec, header_text):
    '''Returns a list of (filename, filecontents) tuples representing the generated files to
    be written to disk'''
    page_view = PageView(page_spec, header_text)
    stache = pystache.Renderer(search_dirs = "templates")

    header_name = header_filename(page_spec)
    header_file = stache.render(stache.load_template("objc_header"), page_view)

    class_name = class_filename(page_spec)
    class_file = stache.render(stache.load_template("objc_class"), page_view)

    return [ (header_name, header_file), (class_name, class_file) ]

def capitalize (string):
    '''capitalizes the first letter of the string, without lower-casing any of the others'''
    return string[0].capitalize() + string[1:]

def get_typename (the_type, pointer_type = True):
    if the_type == s.BoolType:
        typename = BOOL_TYPENAME
    elif the_type == s.StringType:
        typename = STRING_TYPENAME
    else:
        typename = the_type.name

    if not the_type.is_primitive and pointer_type:
        typename += "*"

    return typename

def header_filename (page_spec):
    '''returns the header's filename'''
    return page_spec.name + ".h"

def class_filename (page_spec):
    '''returns the class file's filename'''
    return page_spec.name + ".m"

def get_propname (the_type):
    if the_type.name in s.BASE_TYPES:
        return "MTMutable" + capitalize(the_type.name) + "Prop"
    else:
        return "MTMutablePageProp"

def to_bool (val):
    return "YES" if val else "NO"

class SpecDelegate(object):
    def __init__ (self, delegate):
        self._delegate = delegate

    def __getattr__ (self, name):
        return getattr(self._delegate, name)

class AttrView(SpecDelegate):
    def __init__ (self, attr):
        SpecDelegate.__init__(self, attr)
        self.attr = attr

class PropView(SpecDelegate):
    def __init__ (self, prop):
        SpecDelegate.__init__(self, prop)
        self.prop = prop
        self.attr_dict = {}
        for attr in self.prop.attrs:
            self.attr_dict[attr.name] = attr.value

    def exposed_type (self):
        type_spec = self.prop.type
        if type_spec.type == s.PageRefType:
            return get_typename(type_spec.subtype)
        else:
            return get_typename(type_spec.type)

    def actual_type (self):
        type_spec = self.prop.type
        return get_propname(type_spec.type)

    def nullable (self):
        return to_bool(self.attr_dict.get("nullable"))

class PageView(SpecDelegate):
    def __init__ (self, page, header_text):
        SpecDelegate.__init__(self, page)
        self.page = page
        self.header = header_text

    def superclass (self):
        return self.page.superclass or BASE_PAGE_CLASS
    def props (self):
        return [ PropView(prop) for prop in self.page.props ]
    def header_imports (self):
        return { "name": self.superclass() }
    def class_imports (self):
        return { "name": self.name }

if __name__ == "__main__":

    ANOTHER_PAGE_TYPE = s.Type(name="AnotherPage", is_primitive = False, has_subtype = False)

    PAGE = s.PageSpec(name = "TestPage",
        superclass = None,
        props = [
            s.PropSpec(type = s.TypeSpec(s.BoolType, None), name = "foo", attrs = [], pos = 0),
            s.PropSpec(type = s.TypeSpec(s.PageRefType, ANOTHER_PAGE_TYPE), name = "bar", attrs = [], pos = 0)
        ],
        pos = 0)

    print generate(PAGE)
