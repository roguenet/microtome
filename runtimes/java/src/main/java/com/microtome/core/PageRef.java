package com.microtome.core;

import com.microtome.Library;
import com.microtome.Page;
import com.microtome.error.MicrotomeError;

public class PageRef {
    public static PageRef fromPage (Page page) {
        PageRef ref = new PageRef(page.qualifiedName());
        ref._page = page;
        return ref;
    }

    public PageRef (String pageName) {
        _pageName = pageName;
    }

    public String pageName () {
        return _pageName;
    }

    public Page page () {
        return _page != null && _page.library() != null ? _page : null;
    }

    public void resolve (Library lib, Class pageClass) {
        LibraryItem item = lib.getItemWithQualifiedName(_pageName);
        if (item == null) {
            throw new MicrotomeError("No such item [name=" + _pageName + "]");
        } else if (!pageClass.isInstance(item)) {
            throw new MicrotomeError("Wrong page type [name=" + _pageName + ", expectedType=" +
                pageClass.getSimpleName() + ", actualType=" + item.getClass().getSimpleName() +
                "]");
        }
        _page = (Page)item;
    }

    public PageRef cloneRef () {
        PageRef out = new PageRef(_pageName);
        out._page = _page;
        return out;
    }

    protected final String _pageName;

    protected Page _page;
}
