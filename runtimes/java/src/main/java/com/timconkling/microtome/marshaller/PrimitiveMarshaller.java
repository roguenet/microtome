package com.timconkling.microtome.marshaller;

import com.timconkling.microtome.core.DataReader;
import com.timconkling.microtome.core.MicrotomeMgr;
import com.timconkling.microtome.core.TypeInfo;
import com.timconkling.microtome.core.WritableObject;
import com.timconkling.microtome.error.MicrotomeError;
import com.timconkling.microtome.prop.Prop;

public abstract class PrimitiveMarshaller<T> implements DataMarshaller<T> {
    public PrimitiveMarshaller (Class<T> valueClazz) {
        _valueClazz = valueClazz;
    }

    public final Class<T> valueClass () {
        return _valueClazz;
    }

    @Override public boolean handlesSubclasses () {
        return false;
    }

    @Override public boolean canReadValue (DataReader reader, String name) {
        return reader.hasValue(name);
    }

    @Override public DataReader getValueReader (DataReader parentReader, String name) {
        return parentReader;
    }

    @Override public WritableObject getValueWriter (WritableObject parentWriter, String name) {
        return parentWriter;
    }

    @Override public void resolveRefs (MicrotomeMgr mgr, T value, TypeInfo type) {
        // primitives don't store refs
    }

    @Override public T cloneData (MicrotomeMgr mgr, T data, TypeInfo type) {
        // primitives don't need cloning
        return data;
    }

    @Override public void validateProp (Prop<T> prop) {
        throw new MicrotomeError("Primitive values do not have props by default.");
    }

    protected final Class<T> _valueClazz;
}
