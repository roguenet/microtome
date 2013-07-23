package com.microtome;

import com.microtome.core.LibraryItem;
import com.microtome.core.ReadableObject;
import com.microtome.core.WritableObject;
import com.microtome.marshaller.DataMarshaller;
import java.util.List;

public interface MicrotomeCtx {
    void registerPageClasses (List<Class<?>> classes);

    void registerDataMarshaller (DataMarshaller marshaller);

    void load (Library library, List<ReadableObject> data);
    void write (LibraryItem item, WritableObject writer);
    <T extends LibraryItem> T cloneItem (T item);
}
