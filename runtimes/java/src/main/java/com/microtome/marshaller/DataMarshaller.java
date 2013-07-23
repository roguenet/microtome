package com.microtome.marshaller;

import com.microtome.core.Annotation;
import com.microtome.core.DataReader;
import com.microtome.core.MicrotomeMgr;
import com.microtome.core.TypeInfo;
import com.microtome.core.WritableObject;
import com.microtome.prop.Prop;

public interface DataMarshaller<T> {
    /** The class that this marshaller handles */
    Class<T> valueClass ();

    /** @return true if the marshaller handles subclasses of its valueClass */
    boolean handlesSubclasses ();

    /** @return true if this marshaller can read the named value from this given reader. */
    boolean canReadValue (DataReader reader, String name);

    /** @return a DataReader for the given property on the given DataReader */
    DataReader getValueReader (DataReader parentReader, String name);

    /** @return a WritableObject for the given property on the given WritableObject */
    WritableObject getValueWriter (WritableObject parentWriter, String name);

    /**
     * Reads data using a data reader.
     * @throws com.microtome.error.MicrotomeError if the data cannot be loaded for any
     *         reason.
     */
    T readValue (MicrotomeMgr mgr, DataReader reader, String name, TypeInfo type);

    /**
     * Reads data from a prop's annotations.
     * @throw LoadError if the default cannot be used for any reason.
     */
    T readDefault (MicrotomeMgr mgr, TypeInfo type, Annotation anno);

    /** Writes an object's value. */
    void writeValue (MicrotomeMgr mgr, WritableObject writer, T value, String name, TypeInfo type);

    /** Resolves PageRefs contained within an object. */
    void resolveRefs (MicrotomeMgr mgr, T value, TypeInfo type);

    /**
     * Validates a prop's value, possibly using the annotations on the prop.
     * @throws com.microtome.error.MicrotomeError on failure.
     */
    void validateProp (Prop<T> prop);

    /** @return a clone of the given data. */
    T cloneData (MicrotomeMgr mgr, T data, TypeInfo type);
}
