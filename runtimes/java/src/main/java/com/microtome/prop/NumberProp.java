package com.microtome.prop;

import com.microtome.MutablePage;

public class NumberProp extends Prop<Float> {
    public NumberProp (MutablePage page, PropSpec spec) {
        super(page, spec);
    }

    @Override public Float value () {
        return _value;
    }

    @Override public void setValue (Float val) {
        _value = val;
    }

    protected float _value;
}
