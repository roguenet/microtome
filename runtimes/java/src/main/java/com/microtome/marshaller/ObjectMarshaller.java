package com.microtome.marshaller;

import com.microtome.core.Annotation;
import com.microtome.core.DataReader;
import com.microtome.core.MicrotomeMgr;
import com.microtome.core.TypeInfo;
import com.microtome.core.WritableObject;
import com.microtome.error.MicrotomeError;
import com.microtome.prop.Prop;

public abstract class ObjectMarshaller<T> implements DataMarshaller<T> {
    /**
     * @param isSimple true if this marshaller represents a "simple" data type. A simple type is
     *                 one that can be parsed from a single value. Generally, composite objects
     *                 are likely to be non-simple (though, for example, a Tuple object could be
     *                 made simple if you were to parse it from a comma-delimited string).
     */
    public ObjectMarshaller (boolean isSimple) {
        _isSimple = isSimple;
    }

    @Override public boolean handlesSubclasses () {
        return false;
    }

    @Override public boolean canReadValue (DataReader reader, String name) {
        return _isSimple ? reader.hasValue(name) : reader.hasChild(name);
    }

    @Override public DataReader getValueReader (DataReader parentReader, String name) {
        return _isSimple ? parentReader : parentReader.requireChild(name);
    }

    @Override public WritableObject getValueWriter (WritableObject parentWriter, String name) {
        return _isSimple ? parentWriter : parentWriter.addChild(name, false);
    }

    @Override public void resolveRefs (MicrotomeMgr mgr, T value, TypeInfo type) {
        // do nothing by default.
    }

    @Override public T cloneData (MicrotomeMgr mgr, T data, TypeInfo type) {
        // handle null data
        return data == null ? null : cloneObject(mgr, data, type);
    }

    public abstract T cloneObject (MicrotomeMgr mgr, T data, TypeInfo type);

    @Override public void validateProp (Prop<T> prop) {
        if (!prop.nullable() && prop.value() == null) {
            throw new MicrotomeError("Null value for non-nullable prop [" + prop.name() + "]");
        } else if (prop.value() != null && !valueClass().isInstance(prop.value())) {
            throw new MicrotomeError("Incompatible value type [required=" +
                valueClass().getSimpleName() + ", actual=" +
                prop.value().getClass().getSimpleName() + "]");
        }
    }

    @Override public T readDefault (MicrotomeMgr mgr, TypeInfo type, Annotation anno) {
        throw new MicrotomeError(valueClass().getSimpleName() + " has no default");
    }

    protected final boolean _isSimple;
}
