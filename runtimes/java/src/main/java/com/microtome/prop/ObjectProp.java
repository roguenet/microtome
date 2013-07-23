package com.microtome.prop;

import com.microtome.MutablePage;
import com.microtome.core.LibraryItemBase;

public final class ObjectProp extends Prop<Object> {
    public ObjectProp (MutablePage page, PropSpec spec) {
        super(page, spec);
    }

    @Override public Object value () {
        return _value;
    }

    @Override public void setValue (Object val) {
        if (_value != null && _value.equals(val)) {
            return;
        }

        if (_value instanceof LibraryItemBase) {
            ((LibraryItemBase)_value).setParent(null);
        }
        _value = val;
        if (_value instanceof LibraryItemBase) {
            ((LibraryItemBase)_value).setParent(_page);
        }
    }

    protected Object _value;
}
