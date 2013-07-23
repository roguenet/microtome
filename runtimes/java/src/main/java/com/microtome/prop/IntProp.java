package com.microtome.prop;

import com.microtome.MutablePage;

public class IntProp extends Prop<Integer> {
    public IntProp (MutablePage page, PropSpec spec) {
        super(page, spec);
    }

    @Override public Integer value () {
        return _value;
    }

    @Override public void setValue (Integer val) {
        _value = val;
    }

    protected Integer _value;
}
