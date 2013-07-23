package com.microtome.marshaller;

import com.microtome.core.Annotation;
import com.microtome.core.DataReader;
import com.microtome.core.MicrotomeMgr;
import com.microtome.core.TypeInfo;
import com.microtome.core.WritableObject;
import com.microtome.prop.Prop;

public class BoolMarshaller extends PrimitiveMarshaller {
    public BoolMarshaller () {
        super(Boolean.class);
    }

    @Override public Object readValue (MicrotomeMgr mgr, DataReader reader, String name,
            TypeInfo type) {
        return reader.requireBool(name);
    }

    @Override public Object readDefault (MicrotomeMgr mgr, TypeInfo type, Annotation anno) {
        return anno.boolValue(false);
    }

    @Override public void writeValue (MicrotomeMgr mgr, WritableObject writer, Object value,
            String name, TypeInfo type) {
        writer.writeBool(name, (Boolean)value);
    }

    @Override public void validateProp (Prop prop) {
        //To change body of implemented methods use File | Settings | File Templates.
    }
}
