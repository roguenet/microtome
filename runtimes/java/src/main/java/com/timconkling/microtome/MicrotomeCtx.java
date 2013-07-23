package com.timconkling.microtome;

import com.timconkling.microtome.core.LibraryItem;
import com.timconkling.microtome.core.ReadableObject;
import com.timconkling.microtome.core.WritableObject;
import com.timconkling.microtome.marshaller.DataMarshaller;
import java.util.List;

public interface MicrotomeCtx {
    void registerPageClasses (List<Class<?>> classes);

    void registerDataMarshaller (DataMarshaller marshaller);

    void load (Library library, List<ReadableObject> data);
    void write (LibraryItem item, WritableObject writer);
    <T extends LibraryItem> T cloneItem (T item);
}
