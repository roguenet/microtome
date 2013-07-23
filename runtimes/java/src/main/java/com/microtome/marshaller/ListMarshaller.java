package com.microtome.marshaller;

import com.microtome.core.DataReader;
import com.microtome.core.MicrotomeItem;
import com.microtome.core.MicrotomeMgr;
import com.microtome.core.TypeInfo;
import com.microtome.core.WritableObject;
import java.util.ArrayList;
import java.util.List;

public class ListMarshaller extends ObjectMarshaller<List> {
    public ListMarshaller () {
        super(false);
    }

    @Override public Class<List> valueClass () {
        return List.class;
    }

    @Override public WritableObject getValueWriter (WritableObject parentWriter, String name) {
        return parentWriter.addChild(name, true);
    }

    @Override
    public List readValue (MicrotomeMgr mgr, DataReader reader, String name, TypeInfo type) {
        List list = new ArrayList();
        DataMarshaller childMarshaller = mgr.requireDataMarshaller(type.subtype().clazz());
        for (DataReader childReader : reader.children()) {
            Object child = childMarshaller.readValue(
                mgr, childReader, childReader.name(), type.subtype());
            list.add(child);
        }
        return list;
    }

    @SuppressWarnings("unchecked")
    @Override public void writeValue (MicrotomeMgr mgr, WritableObject writer, List value,
            String name, TypeInfo type) {
        DataMarshaller childMarshaller = mgr.requireDataMarshaller(type.subtype().clazz());
        for (Object child : value) {
            String childName =
                child instanceof MicrotomeItem ? ((MicrotomeItem)child).name() : "item";
            childMarshaller.writeValue(mgr, childMarshaller.getValueWriter(
                writer, childName), child, childName, type.subtype());
        }
    }

    @SuppressWarnings("unchecked")
    @Override public void resolveRefs (MicrotomeMgr mgr, List value, TypeInfo type) {
        DataMarshaller childMarshaller = mgr.requireDataMarshaller(type.subtype().clazz());
        for (Object child : value) {
            childMarshaller.resolveRefs(mgr, child, type.subtype());
        }
    }

    @Override public List cloneObject (MicrotomeMgr mgr, List data, TypeInfo type) {
        DataMarshaller childMarshaller = mgr.requireDataMarshaller(type.subtype().clazz());
        List clone = new ArrayList();
        for (Object child : data) {
            clone.add(childMarshaller.cloneData(mgr, child, type.subtype()));
        }
        return clone;
    }
}
