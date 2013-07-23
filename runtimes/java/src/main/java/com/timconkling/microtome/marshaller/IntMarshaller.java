package com.timconkling.microtome.marshaller;

import com.timconkling.microtome.core.Annotation;
import com.timconkling.microtome.core.DataReader;
import com.timconkling.microtome.core.Defs;
import com.timconkling.microtome.core.MicrotomeMgr;
import com.timconkling.microtome.core.TypeInfo;
import com.timconkling.microtome.core.WritableObject;
import com.timconkling.microtome.error.MicrotomeError;
import com.timconkling.microtome.prop.Prop;

public class IntMarshaller extends PrimitiveMarshaller<Integer> {
    public IntMarshaller () {
        super(Integer.class);
    }

    @Override public void validateProp (Prop<Integer> prop) {
        int min = prop.annotation(Defs.MIN_ANNOTATION).intValue(Integer.MIN_VALUE);
        if (prop.value() < min) {
            throw new MicrotomeError("Value too small [" + prop.value() + " < " + min + "]");
        }
        int max = prop.annotation(Defs.MAX_ANNOATATION).intValue(Integer.MAX_VALUE);
        if (prop.value() > max) {
            throw new MicrotomeError("Value too large [" + prop.value() + " > " + max + "]");
        }
    }

    @Override public Integer readValue (MicrotomeMgr mgr, DataReader reader, String name,
            TypeInfo type) {
        return reader.requireInt(name);
    }

    @Override public Integer readDefault (MicrotomeMgr mgr, TypeInfo type, Annotation anno) {
        return anno.intValue(0);
    }

    @Override public void writeValue (MicrotomeMgr mgr, WritableObject writer, Integer value,
            String name, TypeInfo type) {
        writer.writeInt(name, value);
    }
}
