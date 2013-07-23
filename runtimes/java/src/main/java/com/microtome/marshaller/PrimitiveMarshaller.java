package com.microtome.marshaller;

import com.microtome.core.DataReader;
import com.microtome.core.MicrotomeMgr;
import com.microtome.core.TypeInfo;
import com.microtome.core.WritableObject;
import com.microtome.error.MicrotomeError;
import com.microtome.prop.Prop;

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
