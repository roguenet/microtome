package com.microtome.marshaller;

import com.microtome.MutablePage;
import com.microtome.core.DataReader;
import com.microtome.core.MicrotomeMgr;
import com.microtome.Page;
import com.microtome.core.TypeInfo;
import com.microtome.core.WritableObject;
import com.microtome.error.MicrotomeError;
import com.microtome.prop.Prop;
import java.lang.reflect.Constructor;
import java.util.List;

public class PageMarshaller extends ObjectMarshaller<Page> {
    public PageMarshaller () {
        super(false);
    }

    @Override public Class<Page> valueClass () {
        return Page.class;
    }

    @Override public boolean handlesSubclasses () {
        return true;
    }

    @Override public Page readValue (MicrotomeMgr mgr, DataReader reader, String name,
            TypeInfo type) {
        return mgr.loadPage(reader, type.clazz());
    }

    @Override public void writeValue (MicrotomeMgr mgr, WritableObject writer, Page value,
            String name, TypeInfo type) {
        mgr.writePage(writer, (MutablePage)value);
    }

    @SuppressWarnings("unchecked")
    @Override public void resolveRefs (MicrotomeMgr mgr, Page value, TypeInfo type) {
        MutablePage page = (MutablePage)value;
        for (Prop prop : page.props()) {
            if (prop != null && prop.value() != null) {
                DataMarshaller marshaller = mgr.requireDataMarshaller(
                    prop.valueType().clazz());
                marshaller.resolveRefs(mgr, prop.value(), prop.valueType());
            }
        }
    }

    @SuppressWarnings("unchecked")
    @Override public Page cloneObject (MicrotomeMgr mgr, Page data, TypeInfo type) {
        MutablePage page = (MutablePage)data;
        MutablePage clone;
        try {
            Constructor ctor = data.getClass().getConstructor(String.class);
            clone = (MutablePage)ctor.newInstance(data.name());
        } catch (Exception e) {
            throw new MicrotomeError("Unable to clone page [page=" + data.name() + "]");
        }

        List<Prop> props = page.props();
        List<Prop> cloneProps = clone.props();
        for (int ii = 0; ii < props.size(); ii++) {
            Prop prop = props.get(ii);
            Prop cloneProp = cloneProps.get(ii);
            DataMarshaller marshaller = mgr.requireDataMarshaller(prop.valueType().clazz());
            cloneProp.setValue(marshaller.cloneData(mgr, prop.value(), prop.valueType()));
        }
        return clone;
    }
}
