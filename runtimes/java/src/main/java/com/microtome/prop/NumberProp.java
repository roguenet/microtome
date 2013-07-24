package com.microtome.prop;

import com.microtome.MutablePage;

public class NumberProp extends Prop<Double> {
    public NumberProp (MutablePage page, PropSpec spec) {
        super(page, spec);
    }

    @Override public Double value () {
        return _value;
    }

    @Override public void setValue (Double val) {
        _value = val;
    }

    protected double _value;
}
