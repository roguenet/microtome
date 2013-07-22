package com.timconkling.microtome;

import com.timconkling.microtome.marshaller.DataMarshaller;
import java.util.List;

public interface MicrotomeCtx {
    void registerPageClasses (List<Class<?>> classes);

    void registerDataMarshaller (DataMarshaller marshaller);
}
