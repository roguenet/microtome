package com.microtome.marshaller;

import com.microtome.core.Annotation;
import com.microtome.core.DataReader;
import com.microtome.core.MicrotomeMgr;
import com.microtome.core.TypeInfo;
import com.microtome.core.WritableObject;

public class StringMarshaller extends ObjectMarshaller<String> {
    public StringMarshaller () {
        super(true);
    }

    @Override public Class<String> valueClass () {
        return String.class;
    }

    @Override public String readValue (MicrotomeMgr mgr, DataReader reader, String name,
            TypeInfo type) {
        return reader.requireString(name);
    }

    @Override public String readDefault (MicrotomeMgr mgr, TypeInfo type, Annotation anno) {
        return anno.stringValue("");
    }

    @Override public void writeValue (MicrotomeMgr mgr, WritableObject writer, String value,
            String name, TypeInfo type) {
        writer.writeString(name, value);
    }

    @Override public String cloneObject (MicrotomeMgr mgr, String data, TypeInfo type) {
        // Strings don't need cloning
        return data;
    }
}
