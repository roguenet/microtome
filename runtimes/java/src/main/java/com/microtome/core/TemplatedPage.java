package com.microtome.core;

import com.microtome.MutablePage;

public class TemplatedPage {
    public TemplatedPage (MutablePage page, DataReader reader) {
        _page = page;
        _reader = reader;
    }

    public MutablePage page () {
        return _page;
    }

    public DataReader reader () {
        return _reader;
    }

    public String templateName () {
        return _reader.requireString(Defs.TEMPLATE_ATTR);
    }

    protected final MutablePage _page;
    protected final DataReader _reader;
}
