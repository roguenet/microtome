package com.microtome;

import com.microtome.core.LibraryItem;
import com.microtome.core.LibraryItemBase;
import com.microtome.core.TypeInfo;
import com.microtome.error.MicrotomeError;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MutableTome extends LibraryItemBase implements Tome {
    public MutableTome (String name, Class<?> pageClass) {
        super(name);
        List<Class<?>> classes = new ArrayList<Class<?>>(2);
        classes.add(MutableTome.class);
        classes.add(pageClass);
        _type = TypeInfo.fromClasses(classes);
    }

    @Override public TypeInfo typeInfo () {
        return _type;
    }

    @Override public Class<?> pageClass () {
        return _type.subtype().clazz();
    }

    @Override public int length () {
        return _pages.size();
    }

    @Override public List<LibraryItem> children () {
        List<LibraryItem> children = new ArrayList<LibraryItem>(_pages.size());
        for (MutablePage page : _pages.values()) {
            children.add(page);
        }
        return children;
    }

    @Override public Object childNamed (String name) {
        return getPage(name);
    }

    @Override public boolean hasPage (String name) {
        return _pages.containsKey(name);
    }

    @Override public Page getPage (String name) {
        return _pages.get(name);
    }

    @Override public Page requirePage (String name) {
        Page page = getPage(name);
        if (page == null) {
            throw new MicrotomeError("Missing required page [name=" + name + "]");
        }
        return page;
    }

    public Iterable<MutablePage> pages () {
        return _pages.values();
    }

    public void addPage (MutablePage page) {
        if (!pageClass().isInstance(page)) {
            throw new MicrotomeError("Incorrect page type [req=" + pageClass().getSimpleName() +
                ", got=" + page.getClass().getSimpleName() + "]");
        } else if (page.name() == null) {
            throw new MicrotomeError("Page is missing name [type=" +
                page.getClass().getSimpleName() + "]");
        } else if (_pages.containsKey(page.name())) {
            throw new MicrotomeError("Duplicate page name '" + page.name() + "'");
        } else if (page.parent() != null) {
            throw new MicrotomeError("Page is already parented [parent=" + page.parent() + "]");
        }

        page.setParent(this);
        _pages.put(page.name(), page);
    }

    public void removePage (MutablePage page) {
        if (page.parent() != this) {
            throw new MicrotomeError("Page is not in this tome [page=" + page + "]");
        }
        page.setParent(null);
        _pages.remove(page.name());
    }

    protected final TypeInfo _type;
    protected final Map<String, MutablePage> _pages = new HashMap<String, MutablePage>();
}
