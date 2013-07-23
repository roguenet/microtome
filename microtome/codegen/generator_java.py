import numbers
import os

import pystache

import microtome.codegen.spec as s
import microtome.codegen.util as util


BASE_PAGE_CLASS = "com.microtome.MutablePage"
BASE_PAGE_INTERFACE = "com.microtome.Page"

# Wrapped types so we can .class in the templates
JAVA_TYPENAMES = {
    s.BoolType: "Boolean",
    s.IntType: "Integer",
    s.FloatType: "Float",
    s.StringType: "String",
    s.ListType: "java.util.List",
    s.PageRefType: "com.microtome.core.PageRef",
    s.TomeType: "com.microtome.Tome"
}

JAVA_MUTABLE_TYPENAMES = {
    s.TomeType: "com.microtome.MutableTome"
}

PRIMITIVE_PROPNAMES = {
    s.BoolType: "com.microtome.prop.BoolProp",
    s.IntType: "com.microtome.prop.IntProp",
    s.FloatType: "com.microtome.prop.NumberProp"
}

OBJECT_PROPNAME = "com.microtome.prop.ObjectProp"

LIBRARY_FILENAME = "MicrotomePages.java"
TEMPLATES_DIR = util.abspath("templates/java")

# stuff we always import
BASE_CLASS_IMPORTS = set(["com.microtome.prop.Prop", "com.microtome.prop.PropSpec"])
BASE_INTERFACE_IMPORTS = set()
# stuff we never import (packageless typenames: Boolean, int, etc)
DISCARD_IMPORTS = set([name for name in JAVA_TYPENAMES.values() if util.get_namespace(name) == ""])


def comment_prefix():
    return "//"

def generate_library(lib):
    '''Returns a list of (filename, filecontents) tuples representing the generated files to
    be written to disk'''

    # "escape" param disables html-escaping
    stache = pystache.Renderer(search_dirs=TEMPLATES_DIR, escape=lambda u: u)

    page_types = [PageType(mutable_page_name(spec.name), util.qualified_name(spec.namespace, mutable_page_name(spec.name))) for spec in lib.pages]

    library_view = {
        "namespace": lib.namespace,
        "page_types": sorted(set(page_types)),
        "header": lib.header_text}

    class_contents = stache.render(stache.load_template(LIBRARY_FILENAME), library_view)

    path = util.namespace_to_path(lib.namespace)

    return [(os.path.join(path, LIBRARY_FILENAME), class_contents)]


def generate_page(lib, page_spec):
    '''Returns a list of (filename, filecontents) tuples representing the generated files to
    be written to disk'''
    page_view = PageView(lib, page_spec)

     # "escape" param disables html-escaping
    stache = pystache.Renderer(search_dirs=TEMPLATES_DIR, escape=lambda u: u)

    iface_name = page_view.interface_filename
    iface_contents = stache.render(stache.load_template("Page.java"), page_view)

    class_name = page_view.class_filename
    class_contents = stache.render(stache.load_template("MutablePage.java"), page_view)

    return [(iface_name, iface_contents), (class_name, class_contents)]


def is_page_name(lib, the_type):
    return (util.strip_namespace(the_type) in [page_spec.name for page_spec in lib.pages])


def get_java_typename(lib, the_type, mutable=False):
    '''converts a microtome typename to a Java typename'''
    if mutable and the_type in JAVA_MUTABLE_TYPENAMES:
        return JAVA_MUTABLE_TYPENAMES[the_type]
    elif the_type in JAVA_TYPENAMES:
        return JAVA_TYPENAMES[the_type]
    elif mutable and is_page_name(lib, the_type):
        return mutable_page_name(the_type)
    else:
        return the_type


def get_prop_typename(the_type):
    '''returns the prop typename for the given typename'''
    if the_type in PRIMITIVE_PROPNAMES:
        return PRIMITIVE_PROPNAMES[the_type]
    else:
        return OBJECT_PROPNAME


def to_bool(val):
    return "true" if val else "false"


def mutable_page_name(page_name):
    return util.qualified_name(util.get_namespace(page_name),
                               "Mutable" + util.strip_namespace(page_name))

class PageType(object):
    def __init__(self, classname, qualified):
        self.classname = classname;
        self.qualified = qualified;

class AnnotationView(object):
    def __init__(self, lib, annotation):
        self.annotation = annotation

    @property
    def name(self):
        return self.annotation.name

    @property
    def value(self):
        # bools are Numbers, so do the bool check first
        if isinstance(self.annotation.value, bool):
            return to_bool(self.annotation.value)
        elif isinstance(self.annotation.value, numbers.Number):
            return self.annotation.value
        else:
            return '"' + self.annotation.value + '"'


class TypeView(object):
    def __init__(self, lib, type):
        self.type = type
        self.lib = lib

    @property
    def is_mutable(self):
        return self.type.name == s.PageRefType or \
            self.type.name in JAVA_MUTABLE_TYPENAMES or \
            is_page_name(self.lib, self.type.name)

    @property
    def is_primitive(self):
        return self.type.name in s.PRIMITIVE_TYPES

    @property
    def is_pageref(self):
        return self.type.name == s.PageRefType

    @property
    def name(self):
        return util.strip_namespace(self.get_qualified_name(False))

    @property
    def mutable_name(self):
        return util.strip_namespace(self.get_qualified_name(True))

    @property
    def all_typenames(self):
        return [util.strip_namespace(name) for name in self.qualified_typenames(True)]

    def get_qualified_name(self, mutable):
        if self.type.name == s.PageRefType:
            return get_java_typename(self.lib, self.type.subtype.name, mutable)
        else:
            return get_java_typename(self.lib, self.type.name, mutable)

    def qualified_typenames(self, mutable):
        '''namespace-qualified typenames of all types used by this Type'''
        return [get_java_typename(self.lib, name, mutable) for name in s.type_spec_to_list(self.type)]


class PropView(object):
    def __init__(self, lib, prop):
        self.prop = prop
        self.value_type = TypeView(lib, prop.type)
        self.annotations = [AnnotationView(lib, a) for a in prop.annotations]

    @property
    def typename(self):
        return util.strip_namespace(self.qualified_typename)

    @property
    def qualified_typename(self):
        return get_prop_typename(self.prop.type.name)

    @property
    def name(self):
        return self.prop.name

    @property
    def mutable_name(self):
        return "mutable" + util.uppercase_first(self.name)

    @property
    def annotation_text(self):
        # avoid obnoxious mustache markup
        if not self.has_annos:
            return "null"
        out = "PropSpec.annotations("
        needs_separator = False
        for a in self.annotations:
            if needs_separator:
                out += ", "
            out += '"' + a.name + '"' + ", " + str(a.value)
            needs_separator = True
        out += ")"
        return out

    @property
    def has_annos(self):
        return len(self.annotations) > 0


class PageView(object):
    def __init__(self, lib, page):
        self.lib = lib
        self.page = page
        self.header = lib.header_text
        self.props = [PropView(lib, prop) for prop in self.page.props]

    @property
    def class_name(self):
        return mutable_page_name(self.page.name)

    @property
    def interface_name(self):
        return self.page.name

    @property
    def superclass(self):
        return util.strip_namespace(self.qualified_superclass)

    @property
    def qualified_superclass(self):
        if self.page.superclass is None:
            return BASE_PAGE_CLASS
        else:
            super_namespace = util.get_namespace(self.page.superclass)
            super_name = mutable_page_name(util.strip_namespace(self.page.superclass))
            return util.qualified_name(super_namespace, super_name)

    @property
    def parent_interface(self):
        return util.strip_namespace(self.qualified_parent_interface)

    @property
    def qualified_parent_interface(self):
        return self.page.superclass or BASE_PAGE_INTERFACE

    @property
    def namespace(self):
        return self.page.namespace

    @property
    def class_filename(self):
        return os.path.join(util.namespace_to_path(self.namespace), self.class_name + ".java")

    @property
    def interface_filename(self):
        return os.path.join(util.namespace_to_path(self.namespace), self.interface_name + ".java")

    @property
    def class_imports(self):
        # prop classes
        imp_list = [prop.qualified_typename for prop in self.props]
        # our own superclass
        imp_list.append(self.qualified_superclass)
        imp_list.append("java.util.List")
        # prop value typenames
        for prop in self.props:
            imp_list += prop.value_type.qualified_typenames(False)
            if prop.value_type.is_mutable:
                imp_list += prop.value_type.qualified_typenames(True)

        # strip out anything in our namespace
        imp_list = [imp for imp in imp_list if not self.same_namespace(imp)]

        # remove the imports we never want; add the imports we always want
        imports = set(imp_list) - DISCARD_IMPORTS | BASE_CLASS_IMPORTS

        return sorted(imports)

    def interface_imports(self):
        # prop value classes
        imp_list = [prop.value_type.get_qualified_name(False) for prop in self.props]
        # our own superclass
        imp_list.append(self.qualified_parent_interface)

        # strip out anything in our namespace
        imp_list = [imp for imp in imp_list if not self.same_namespace(imp)]

        # remove the imports we never want; add the imports we always want
        imports = set(imp_list) - DISCARD_IMPORTS | BASE_INTERFACE_IMPORTS

        return sorted(imports)

    def same_namespace(self, typename):
        return self.namespace == util.get_namespace(typename)

if __name__ == "__main__":
    NAMESPACE = "com.microtome.test"
    ANOTHER_PAGE_TYPE = s.TypeSpec("com.microtome.test.AnotherPage", None)

    ANOTHER_PAGE = s.PageSpec(name="AnotherPage",
                              namespace=NAMESPACE,
                              superclass=None,
                              props=[],
                              pos=0)

    PAGE = s.PageSpec(name="TestPage",
        namespace=NAMESPACE,
        superclass=None,
        props=[
            s.PropSpec(type=s.TypeSpec(s.BoolType, None), name="foo", annotations=[
                s.AnnotationSpec(name="default", value="test", pos=0),
                s.AnnotationSpec(name="nullable", value=True, pos=0)
            ], pos=0),
            s.PropSpec(type=s.TypeSpec(s.PageRefType, ANOTHER_PAGE_TYPE), name="bar", annotations=[], pos=0)
        ],
        pos=0)

    LIB = s.LibrarySpec(namespace=NAMESPACE, header_text="", pages=[PAGE, ANOTHER_PAGE])

    for filename, file_contents in generate_page(LIB, PAGE):
        print filename + ":"
        print file_contents

