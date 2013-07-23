package com.microtome.core;

public class TypeInfo {
    public static TypeInfo fromClasses (Class<?>[] classes) {
        TypeInfo last = null;
        for (int ii = classes.length - 1; ii >= 0; ii--) {
            Class<?> clazz = classes[ii];
            last = new TypeInfo(clazz, last);
        }
        return last;
    }

    public TypeInfo (Class<?> clazz, TypeInfo subtype) {
        _clazz = clazz;
        _subtype = subtype;
    }

    public Class<?> clazz () {
        return _clazz;
    }

    public TypeInfo subtype () {
        return _subtype;
    }

    protected final Class<?> _clazz;
    protected final TypeInfo _subtype;
}
