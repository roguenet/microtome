#
# microtome - Tim Conkling, 2012

import pystache
import util
import spec as s

BASE_PAGE_CLASS = "MutablePage"

AS3_TYPENAMES = {
    s.BoolType: "Boolean",
    s.IntType: "int",
    s.FloatType: "Number",
    s.StringType: "String",
    s.ListType: "Array"
}

PRIMITIVE_PROPNAMES = {
    s.BoolType: "BoolProp",
    s.IntType: "IntProp",
    s.FloatType: "NumberProp"
}

OBJECT_PROPNAME = "ObjectProp"

LIBRARY_CLASS = "MicrotomePages.as"
TEMPLATES_DIR = util.abspath("templates/as")

def generate_library (page_names, header_text = ""):
    '''Returns a list of (filename, filecontents) tuples representing the generated files to
    be written to disk'''

    # "escape" param disables html-escaping
    stache = pystache.Renderer(search_dirs = TEMPLATES_DIR, escape = lambda u: u)

    library_view = { "page_names": sorted(page_names), "header": header_text }

    class_contents = stache.render(stache.load_template(LIBRARY_CLASS), library_view)

    return [ (LIBRARY_CLASS, class_contents) ]

def generate_page (page_spec, header_text = ""):
    '''Returns a list of (filename, filecontents) tuples representing the generated files to
    be written to disk'''
    page_view = PageView(page_spec, header_text)

     # "escape" param disables html-escaping
    stache = pystache.Renderer(search_dirs = TEMPLATES_DIR, escape = lambda u: u)

    class_name = page_view.class_filename()
    class_contents = stache.render(stache.load_template("as_class"), page_view)

    return [ (class_name, class_contents) ]

def get_as3_typename (the_type):
    '''converts a microtome typename to an actionscript typename'''
    if the_type in AS3_TYPENAMES:
        return AS3_TYPENAMES[the_type]
    else:
        return the_type

def get_prop_typename (the_type):
    '''returns the prop typename for the given typename'''
    if the_type in PRIMITIVE_PROPNAMES:
        return PRIMITIVE_PROPNAMES[the_type]
    else:
        return OBJECT_PROPNAME

class TypeView(object):
    def __init__ (self, type):
        self.type = type;

    def is_primitive (self):
        return self.type.name in s.PRIMITIVE_TYPES

    def name (self):
        if self.type.name == s.PageRefType:
            return get_as3_typename(self.type.subtype.name)
        else:
            return get_as3_typename(self.type.name)


class PropView(object):
    def __init__ (self, prop):
        self.prop = prop;
        self.value_type = TypeView(prop.type)
        self.annotations = None

    def typename (self):
        return get_prop_typename(self.prop.type.name)

    def name (self):
        return self.prop.name

class PageView(object):
    def __init__ (self, page, header_text):
        self.page = page
        self.header = header_text
        self.props = [ PropView(prop) for prop in self.page.props ]

    def name (self):
        return self.page.name

    def superclass (self):
        return self.page.superclass or BASE_PAGE_CLASS

    def package (self):
        return self.page.package

    def class_filename (self):
        return self.name() + ".as"

if __name__ == "__main__":
    ANOTHER_PAGE_TYPE = s.TypeSpec(name="AnotherPage", subtype = None)

    PAGE = s.PageSpec(name = "TestPage",
        package = "com.microtome.test",
        superclass = None,
        props = [
            s.PropSpec(type = s.TypeSpec(s.BoolType, None), name = "foo", annotations = [
                s.AnnotationSpec(name="default", value="test", pos=0)
            ], pos = 0),
            s.PropSpec(type = s.TypeSpec(s.PageRefType, ANOTHER_PAGE_TYPE), name = "bar", annotations = [], pos = 0)
        ],
        pos = 0)

    for filename, file_contents in generate_page(PAGE):
        print filename + ":\n"
        print file_contents