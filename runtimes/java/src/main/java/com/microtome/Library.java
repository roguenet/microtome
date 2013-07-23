package com.microtome;

import com.microtome.core.Defs;
import com.microtome.core.LibraryItem;
import com.microtome.core.LibraryItemBase;
import com.microtome.core.MicrotomeItem;
import com.microtome.error.MicrotomeError;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Library implements MicrotomeItem {
    @Override public String name () {
        return null;
    }

    @Override public Library library () {
        return null;
    }

    @Override public MicrotomeItem parent () {
        return null;
    }

    @Override public List<LibraryItem> children () {
        List<LibraryItem> children = new ArrayList<LibraryItem>();
        for (LibraryItem child : _items.values()) {
            children.add(child);
        }
        return children;
    }

    public LibraryItem getItemWithQualifiedName (String qualifiedName) {
        // A qualifiedName is a series of pages and tome names, separated by dots
        // E.g. level1.baddies.big_boss

        LibraryItem item = null;
        for (String name : qualifiedName.split(Defs.NAME_SEPARATOR)) {
            Object child = item != null ? item.childNamed(name) : _items.get(name);
            if (!(child instanceof LibraryItem)) {
                return null;
            }
            item = (LibraryItem)child;
        }

        return item;
    }

    public LibraryItem requireItemWithQualifiedName (String qualifiedName) {
        LibraryItem item = getItemWithQualifiedName(qualifiedName);
        if (item == null) {
            throw new MicrotomeError("No such item [qualifiedName=" + qualifiedName + "]");
        }
        return item;
    }

    public Object getItem (String name) {
        return _items.get(name);
    }

    public boolean hasItem (String name) {
        return _items.containsKey(name);
    }

    public void addItem (LibraryItem item) {
        if (hasItem(item.name())) {
            throw new MicrotomeError(
                "An item with that name already exists [name=" + item.name() + "]");
        } else if (item.parent() != null) {
            throw new MicrotomeError("Item is aliready in a library [item=" + item + "]");
        }

        setItemParent(item, this);
        _items.put(item.name(), item);
    }

    public void removeItem (LibraryItem item) {
        if (item.parent() != this) {
            throw new MicrotomeError("Item is not in this library [item=" + item + "]");
        }
        setItemParent(item, null);
        _items.remove(item.name());
    }

    public void removeAllItems () {
        for (LibraryItem item : _items.values()) {
            setItemParent(item, null);
        }
        _items.clear();
    }

    protected void setItemParent (LibraryItem item, Library library) {
        ((LibraryItemBase)item).setParent(library);
    }

    protected final Map<String, LibraryItem> _items = new HashMap<String, LibraryItem>();
}
