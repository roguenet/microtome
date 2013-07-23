package com.timconkling.microtome.marshaller;

import com.timconkling.microtome.MutablePage;
import com.timconkling.microtome.MutableTome;
import com.timconkling.microtome.core.DataReader;
import com.timconkling.microtome.core.MicrotomeMgr;
import com.timconkling.microtome.Page;
import com.timconkling.microtome.core.TypeInfo;
import com.timconkling.microtome.core.WritableObject;

public class TomeMarshaller extends ObjectMarshaller<MutableTome> {
    public TomeMarshaller () {
        super(false);
    }

    @Override public Class<MutableTome> valueClass () {
        return MutableTome.class;
    }

    @Override public MutableTome readValue (MicrotomeMgr mgr, DataReader reader, String name,
            TypeInfo type) {
        return mgr.loadTome(reader, type.subtype().clazz());
    }

    @Override public void writeValue (MicrotomeMgr mgr, WritableObject writer, MutableTome value,
            String name, TypeInfo type) {
        mgr.writeTome(writer, value);
    }

    @SuppressWarnings("unchecked")
    @Override public void resolveRefs (MicrotomeMgr mgr, MutableTome value, TypeInfo type) {
        DataMarshaller pageMarshaller = mgr.requireDataMarshaller(value.pageClass());
        for (Page page : value.pages()) {
            pageMarshaller.resolveRefs(mgr, page, type.subtype());
        }
    }

    @SuppressWarnings("unchecked")
    @Override public MutableTome cloneObject (MicrotomeMgr mgr, MutableTome data, TypeInfo type) {
        DataMarshaller pageMarshaller = mgr.requireDataMarshaller(data.pageClass());
        MutableTome clone = new MutableTome(data.name(), data.pageClass());

        for (Page page : data.pages()) {
            clone.addPage((MutablePage)pageMarshaller.cloneData(mgr, page, type.subtype()));
        }

        return clone;
    }
}
