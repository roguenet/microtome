package com.timconkling.microtome.core;

import com.timconkling.microtome.Library;
import com.timconkling.microtome.error.MicrotomeError;
import com.timconkling.microtome.util.Util;

public abstract class LibraryItemBase implements MicrotomeItem {
    public LibraryItemBase (String name) {
        if (!Util.validLibraryItemName(name)) {
            throw new IllegalArgumentException("Invalid library item name '" + name + "'");
        }
        _name = name;
    }

    /** The item's fully qualified name, used during PageRef resoluation */
    public final String qualifiedName () {
        if (library() == null) {
            throw new MicrotomeError(
                "Item must be in a library to have a fullyQualifiedName [itme=" + this + "]");
        }

        String out = _name;
        MicrotomeItem curItem = _parent;
        while (curItem != null && curItem.library() != curItem) {
            out = curItem.name() + Defs.NAME_SEPARATOR + out;
            curItem = curItem.parent();
        }
        return out;
    }

    @Override public String name () {
        return _name;
    }

    @Override public Library library () {
        return _parent == null ? null : _parent.library();
    }

    @Override public MicrotomeItem parent () {
        return _parent;
    }

    public abstract TypeInfo typeInfo ();

    public abstract Object childNamed (String name);

    public void setParent (MicrotomeItem parent) {
        _parent = parent;
    }

    protected final String _name;

    protected MicrotomeItem _parent;
}
