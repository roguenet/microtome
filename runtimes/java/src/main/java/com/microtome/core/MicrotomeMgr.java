package com.microtome.core;

import com.microtome.Library;
import com.microtome.MicrotomeCtx;
import com.microtome.MutablePage;
import com.microtome.MutableTome;
import com.microtome.error.MicrotomeError;
import com.microtome.marshaller.BoolMarshaller;
import com.microtome.marshaller.DataMarshaller;
import com.microtome.marshaller.IntMarshaller;
import com.microtome.marshaller.ListMarshaller;
import com.microtome.marshaller.NumberMarshaller;
import com.microtome.marshaller.PageMarshaller;
import com.microtome.marshaller.PageRefMarshaller;
import com.microtome.marshaller.StringMarshaller;
import com.microtome.marshaller.TomeMarshaller;
import com.microtome.prop.Prop;
import com.microtome.util.Util;
import java.lang.reflect.Constructor;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class MicrotomeMgr implements MicrotomeCtx {
    public MicrotomeMgr () {
        _pageClasses = new HashMap<String, Class<?>>();
        _dataMarshallers = new HashMap<Class<?>, DataMarshaller<?>>();

        registerDataMarshaller(new BoolMarshaller());
        registerDataMarshaller(new IntMarshaller());
        registerDataMarshaller(new NumberMarshaller());
        registerDataMarshaller(new ListMarshaller());
        registerDataMarshaller(new PageMarshaller());
        registerDataMarshaller(new PageRefMarshaller());
        registerDataMarshaller(new StringMarshaller());
        registerDataMarshaller(new TomeMarshaller());
    }

    public Library library () {
        return _loadTask.get().library();
    }

    @Override public void registerPageClasses (List<Class<?>> classes) {
        for (Class<?> clazz : classes) {
            if (!MutablePage.class.isAssignableFrom(clazz)) {
                throw new MicrotomeError("Class must extend MutablePage [pageClass=" +
                    clazz.getSimpleName() + "]");
            }

            _pageClasses.put(Util.pageTypeName(clazz), clazz);
        }
    }

    @Override public void registerDataMarshaller (DataMarshaller marshaller) {
        _dataMarshallers.put(marshaller.valueClass(), marshaller);
    }

    @SuppressWarnings("unchecked")
    public <T> DataMarshaller<? super T> requireDataMarshaller (Class<T> clazz) {
        DataMarshaller<? super T> marshaller = (DataMarshaller<T>)_dataMarshallers.get(clazz);
        if (marshaller == null) {
            // if we can't find an exact match, see if we have a handler for a superclass that
            // can take subclasses
            for (DataMarshaller<?> candidate : _dataMarshallers.values()) {
                if (candidate.handlesSubclasses() &&
                    candidate.valueClass().isAssignableFrom(clazz)) {
                    marshaller = (DataMarshaller<? super T>)candidate;
                    break;
                }
            }
            if (marshaller != null) _dataMarshallers.put(clazz, marshaller);
        }

        if (marshaller == null) {
            throw new MicrotomeError("No DataMarshaller for '" + clazz.getSimpleName() + "'");
        }
        return marshaller;
    }

    public Class<?> getPageClass (String name) {
        return _pageClasses.get(name);
    }

    public Class<?> requirePageClass (String name, Class<?> requiredSuperclass) {
        Class<?> clazz = getPageClass(name);
        if (clazz == null) {
            throw new MicrotomeError("No such page class [name=" + name + "]");
        } else if (requiredSuperclass != null && !requiredSuperclass.isAssignableFrom(clazz)) {
            throw new MicrotomeError("Unexpected page class [req=" +
                requiredSuperclass.getSimpleName() + ", got=" + clazz.getSimpleName() + "]");
        }
        return clazz;
    }

    public void load (Library library, List<ReadableObject> dataElements) {
        LoadTask loadTask = _loadTask.get();
        if (loadTask != null) {
            throw new MicrotomeError("Load already in progress");
        }
        _loadTask.set(loadTask = new LoadTask(library));

        try {
            for (ReadableObject elt : dataElements) {
                for (DataReader itemReader : new DataReader(elt).children()) {
                    loadTask.addItem(loadLibraryItem(itemReader));
                }
            }

            addLoadedItems(loadTask);

            // Resolve all templated items:
            // Iterate through the array as many times as it takes to resolve all template-dapendent
            // pages (some templates may themselves have templates in pendingTemplatePages).
            boolean foundTemplate = true;
            while (foundTemplate) {
                foundTemplate = false;
                for (int ii = 0; ii < loadTask.pendingTemplatePages.size(); ii++) {
                    TemplatedPage tPage = loadTask.pendingTemplatePages.get(ii);
                    LibraryItem tmpl =
                        loadTask.library().getItemWithQualifiedName(tPage.templateName());
                    if (tmpl != null && !(tmpl instanceof MutablePage)) {
                        throw new MicrotomeError(
                            "Library item expected to be a MutablePage [item=" +
                                tmpl.getClass().getSimpleName() + "]");
                    }
                    if (tmpl != null && !loadTask.isPendingTemplatePage((MutablePage)tmpl)) {
                        loadPageProps(tPage.page(), tPage.reader(), (MutablePage)tmpl);
                        loadTask.pendingTemplatePages.remove(ii);
                        foundTemplate = true;
                        break;
                    }
                }
            }

            // throw an error if we're missing a template
            if (loadTask.pendingTemplatePages.size() > 0) {
                TemplatedPage missing = loadTask.pendingTemplatePages.get(0);
                throw new MicrotomeError("Missing template [name=" + missing.templateName() + "]");
            }

            // finalize the load, which resolves all PageRefs
            finalizeLoadedItems(loadTask);

        } catch (RuntimeException re) {
            abortLoad(loadTask);
            throw re;
        } finally {
            _loadTask.set(null);
        }
    }

    public MutableTome loadTome (DataReader reader, Class<?> pageClass) {
        String name = reader.name();
        if (!Util.validLibraryItemName(name)) {
            throw new MicrotomeError("Invalid tome name [name=" + name + "]");
        }

        MutableTome tome = new MutableTome(name, pageClass);
        for (DataReader pageReader : reader.children()) {
            tome.addPage(loadPage(pageReader, pageClass));
        }
        return tome;
    }

    public MutablePage loadPage (DataReader reader, Class<?> requiredSuperclass) {
        String name = reader.name();
        if (!Util.validLibraryItemName(name)) {
            throw new MicrotomeError("Invalid page name [name=" + name + "]");
        }

        String typename = reader.requireString(Defs.PAGE_TYPE_ATTR);
        Class<?> pageClass = requirePageClass(typename, requiredSuperclass);

        MutablePage page;
        try {
            Constructor<?> ctor = pageClass.getConstructor(String.class);
            page = (MutablePage)ctor.newInstance(name);
        } catch (Exception e) {
            throw new MicrotomeError("No single-arg String constructor [page=" +
                pageClass.getSimpleName() + "]");
        }

        if (reader.hasValue(Defs.TEMPLATE_ATTR)) {
            // if this page has a template, we defer loading until the end
            _loadTask.get().pendingTemplatePages.add(new TemplatedPage(page, reader));
        } else {
            loadPageProps(page, reader, null);
        }

        return page;
    }

    public void write (LibraryItem item, WritableObject writer) {
        WritableObject itemWriter = writer.addChild(item.name(), false);
        if (item instanceof MutablePage) {
            writePage(itemWriter, (MutablePage)item);
        } else if (item instanceof MutableTome) {
            writeTome(itemWriter, (MutableTome)item);
        } else {
            throw new MicrotomeError("Unrecognized LibraryItem [item=" + item + "]");
        }
    }

    @SuppressWarnings("unchecked")
    public void writePage (WritableObject writer, MutablePage page) {
        writer.writeString(Defs.PAGE_TYPE_ATTR, Util.pageTypeName(page.getClass()));

        // TODO: template support...
        for (Prop prop : page.props()) {
            if (prop.value() == null) continue;

            DataMarshaller marshaller = requireDataMarshaller(prop.valueType().clazz());
            WritableObject childWriter = marshaller.getValueWriter(writer, prop.name());
            marshaller.writeValue(this, childWriter, prop.value(), prop.name(), prop.valueType());
        }
    }

    public void writeTome (WritableObject writer, MutableTome tome) {
        writer.writeString(Defs.PAGE_TYPE_ATTR, Util.pageTypeName(tome.pageClass()));
        writer.writeBool(Defs.IS_TOME_ATTR, true);
        List<LibraryItem> children = tome.children();
        Collections.sort(children, SORT_ON_NAME);
        for (LibraryItem item : children) {
            writePage(writer.addChild(item.name(), false), (MutablePage)item);
        }
    }

    @Override public <T extends LibraryItem> T cloneItem (T item) {
        @SuppressWarnings("unchecked")
        DataMarshaller<T> marshaller = (DataMarshaller<T>) requireDataMarshaller(item.getClass());
        return marshaller.cloneData(this, item, item.typeInfo());
    }

    protected void loadPageProps (MutablePage page, DataReader reader, MutablePage tmpl) {
        // template's class must be equal to (or a subclass of) page's class
        if (tmpl != null && !(page.getClass().isAssignableFrom(tmpl.getClass()))) {
            throw new MicrotomeError("Incompatible template [pageName=" + page.name() +
                ", pageClass=" + page.getClass().getSimpleName() + ", templateName=" +
                tmpl.name() + ", templateClass=" + tmpl.getClass().getSimpleName() + "]");
        }

        for (Prop prop : page.props()) {
            // if we have a template, get its corresponding template
            Prop tProp = null;
            if (tmpl != null) {
                tProp = Util.getProp(tmpl, prop.name());
                if (tProp == null) {
                    throw new MicrotomeError("Missing prop in template [template=" +
                        tmpl.name() + ", prop=" + prop.name() + "]");
                }
            }

            // load the prop
            loadPageProp(prop, tProp, reader);
        }
    }

    protected <T> void loadPageProp (Prop<T> prop, Prop<T> tProp, DataReader pageReader) {
        // 1. Read the value from the DataReader, if it exists
        // 2. Else, ropy the value from the template, if it exists
        // 3. Else, read the value from its 'default' annotation, if it exists
        // 4. Else, set the value to null if it's nullable
        // 5. Else, fail.

        String name = prop.name();
        @SuppressWarnings("unchecked")
        DataMarshaller<T> marshaller =
            (DataMarshaller<T>)requireDataMarshaller(prop.valueType().clazz());

        boolean canRead = marshaller.canReadValue(pageReader, name);
        boolean useTemplate = !canRead && (tProp != null);

        if (canRead) {
            DataReader reader = marshaller.getValueReader(pageReader, name);
            prop.setValue(marshaller.readValue(this, reader, name, prop.valueType()));
            marshaller.validateProp(prop);
        } else if (useTemplate) {
            prop.setValue(tProp.value());
        } else if (prop.hasDefault()) {
            prop.setValue(marshaller.readDefault(
                this, prop.valueType(), prop.annotation(Defs.DEFAULT_ANNOTATION)));
        } else if (prop.nullable()) {
            prop.setValue(null);
        } else {
            throw new MicrotomeError("Missing required value of child [child=" + name + ", page=" +
                pageReader.name() + "]");
        }
    }

    protected LibraryItem loadLibraryItem (DataReader reader) {
        // a tome or a page
        String pageType = reader.requireString(Defs.PAGE_TYPE_ATTR);
        if (reader.getBool(Defs.IS_TOME_ATTR, false)) {
            // it's a tome!
            return loadTome(reader, requirePageClass(pageType, null));
        } else {
            // it's a page!
            return loadPage(reader, null);
        }
    }

    protected void addLoadedItems (LoadTask task) {
        if (task.state != LoadTask.State.LOADING) {
            throw new MicrotomeError("task.state != LOADING");
        }

        for (LibraryItem item : task.libraryItems()) {
            if (task.library().hasItem(item.name())) {
                task.state = LoadTask.State.ABORTED;
                throw new MicrotomeError("An item named '" + item.name() + "' is already loaded");
            }
        }

        for (LibraryItem item : task.libraryItems()) {
            task.library().addItem(item);
        }

        task.state = LoadTask.State.ADDED_ITEMS;
    }

    protected void finalizeLoadedItems (LoadTask task) {
        if (task.state != LoadTask.State.ADDED_ITEMS) {
            throw new MicrotomeError("task.state != ADDED_ITEMS");
        }

        try {
            for (LibraryItem item : task.libraryItems()) {
                @SuppressWarnings("unchecked")
                DataMarshaller<LibraryItem> marshaller =
                    (DataMarshaller<LibraryItem>)requireDataMarshaller(item.getClass());
                marshaller.resolveRefs(this, item, item.typeInfo());
            }
        } catch (RuntimeException re) {
            abortLoad(task);
            throw re;
        }

        task.state = LoadTask.State.FINALIZED;
    }

    protected void abortLoad (LoadTask task) {
        if (task.state == LoadTask.State.ABORTED) {
            return;
        }

        for (LibraryItem item : task.libraryItems()) {
            if (task.library() == item.library()) {
                task.library().removeItem(item);
            }
        }
        task.state = LoadTask.State.ABORTED;
    }

    protected static final Comparator<LibraryItem> SORT_ON_NAME = new Comparator<LibraryItem>() {
        @Override public int compare (LibraryItem libraryItem, LibraryItem libraryItem2) {
            return libraryItem.name().compareTo(libraryItem2.name());
        }
    };

    protected final Map<String, Class<?>> _pageClasses;
    protected final Map<Class<?>, DataMarshaller<?>> _dataMarshallers;

    protected ThreadLocal<LoadTask> _loadTask = new ThreadLocal<LoadTask>();
}
