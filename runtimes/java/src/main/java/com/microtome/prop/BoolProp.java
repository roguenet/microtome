package com.microtome.prop;

import com.microtome.MutablePage;

public class BoolProp extends Prop<Boolean> {
    public BoolProp (MutablePage page, PropSpec spec) {
        super(page, spec);
    }

    @Override public Boolean value () {
        return _value;
    }

    @Override public void setValue (Boolean val) {
        _value = val;
    }

    protected boolean _value;
}
