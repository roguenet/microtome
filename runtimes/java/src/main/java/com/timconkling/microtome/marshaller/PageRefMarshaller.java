package com.timconkling.microtome.marshaller;

import com.timconkling.microtome.core.DataReader;
import com.timconkling.microtome.core.MicrotomeMgr;
import com.timconkling.microtome.core.PageRef;
import com.timconkling.microtome.core.TypeInfo;
import com.timconkling.microtome.core.WritableObject;
import com.timconkling.microtome.error.MicrotomeError;

public class PageRefMarshaller extends ObjectMarshaller<PageRef> {
    public PageRefMarshaller () {
        super(true);
    }

    @Override public Class<PageRef> valueClass () {
        return PageRef.class;
    }

    @Override public PageRef readValue (MicrotomeMgr mgr, DataReader reader, String name,
            TypeInfo type) {
        String pageName = reader.requireString(name);
        if (pageName.length() == 0) {
            throw new MicrotomeError("Invalid PageRef [pageName=" + pageName + "]");
        }
        return new PageRef(pageName);
    }

    @Override public void writeValue (MicrotomeMgr mgr, WritableObject writer, PageRef value,
            String name, TypeInfo type) {
        writer.writeString(name, value.pageName());
    }

    @Override public void resolveRefs (MicrotomeMgr mgr, PageRef value, TypeInfo type) {
        value.resolve(mgr.library(), type.subtype().clazz());
    }

    @Override public PageRef cloneObject (MicrotomeMgr mgr, PageRef data, TypeInfo type) {
        return data.cloneRef();
    }
}
