package com.timconkling.microtome.marshaller;

import com.timconkling.microtome.core.Annotation;
import com.timconkling.microtome.core.DataReader;
import com.timconkling.microtome.core.MicrotomeMgr;
import com.timconkling.microtome.core.TypeInfo;
import com.timconkling.microtome.core.WritableObject;
import com.timconkling.microtome.prop.Prop;

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
