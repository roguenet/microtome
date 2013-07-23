package com.microtome.marshaller;

import com.microtome.core.Annotation;
import com.microtome.core.DataReader;
import com.microtome.core.Defs;
import com.microtome.core.MicrotomeMgr;
import com.microtome.core.TypeInfo;
import com.microtome.core.WritableObject;
import com.microtome.error.MicrotomeError;
import com.microtome.prop.Prop;

public class NumberMarshaller extends PrimitiveMarshaller<Float> {
    public NumberMarshaller () {
        super(Float.class);
    }

    @Override public void validateProp (Prop<Float> prop) {
        float min = prop.annotation(Defs.MIN_ANNOTATION).numberValue(Float.MIN_VALUE);
        if (prop.value() < min) {
            throw new MicrotomeError("Value too small [" + prop.value() + " < " + min + "]");
        }
        float max = prop.annotation(Defs.MAX_ANNOATATION).numberValue(Float.MAX_VALUE);
        if (prop.value() > max) {
            throw new MicrotomeError("Value too large [" + prop.value() + " > " + max + "]");
        }
    }

    @Override public Float readValue (MicrotomeMgr mgr, DataReader reader, String name,
            TypeInfo type) {
        return reader.requireNumber(name);
    }

    @Override public Float readDefault (MicrotomeMgr mgr, TypeInfo type, Annotation anno) {
        return anno.numberValue(0);
    }

    @Override public void writeValue (MicrotomeMgr mgr, WritableObject writer, Float value,
            String name, TypeInfo type) {
        writer.writeNumber(name, value);
    }
}
