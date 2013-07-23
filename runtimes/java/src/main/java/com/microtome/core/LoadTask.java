package com.microtome.core;

import com.microtome.Library;
import com.microtome.Page;
import java.util.ArrayList;
import java.util.List;

public class LoadTask {
    public enum State { LOADING, ADDED_ITEMS, FINALIZED, ABORTED }

    public State state = State.LOADING;
    public final List<TemplatedPage> pendingTemplatePages = new ArrayList<TemplatedPage>();

    public LoadTask (Library library) {
        _library = library;
    }

    public Library library () {
        return _library;
    }

    public List<LibraryItem> libraryItems () {
        return _libraryItems;
    }

    public void addItem (LibraryItem item) {
        if (state != State.LOADING) {
            throw new IllegalStateException("state != LOADING");
        }
        _libraryItems.add(item);
    }

    public boolean isPendingTemplatePage (Page page) {
        return pendingTemplatePages.contains(page);
    }

    protected final Library _library;
    protected final List<LibraryItem> _libraryItems = new ArrayList<LibraryItem>();
}
