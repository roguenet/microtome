package com.timconkling.microtome.prop;

import com.timconkling.microtome.MutablePage;
import com.timconkling.microtome.core.Annotation;
import com.timconkling.microtome.core.Defs;
import com.timconkling.microtome.core.TypeInfo;

public abstract class Prop<T> {
    public Prop (MutablePage page, PropSpec spec) {
        _page = page;
        _spec = spec;
    }

    public abstract T value ();

    public abstract void setValue (T val);

    public final TypeInfo valueType () {
        return _spec.valueType;
    }

    public final boolean nullable () {
        return annotation(Defs.NULLABLE_ANNOTATION).boolValue(false);
    }

    public final boolean hasDefault () {
        return hasAnnotation(Defs.DEFAULT_ANNOTATION);
    }

    public final String name () {
        return _spec.name;
    }

    public final boolean hasAnnotation (String name) {
        return _spec.hasAnnotation(name);
    }

    public final Annotation annotation (String name) {
        return _spec.getAnnotation(name);
    }

    protected final MutablePage _page;
    protected final PropSpec _spec;
}
