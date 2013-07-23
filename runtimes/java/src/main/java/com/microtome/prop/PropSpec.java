package com.microtome.prop;

import com.microtome.core.Annotation;
import com.microtome.core.TypeInfo;
import com.microtome.error.MicrotomeError;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class PropSpec {
    public String name;
    public TypeInfo valueType;

    public static Map<String, Annotation> annotations (Object... args) {
        Map<String, Annotation> map = new HashMap<String, Annotation>();
        if (args.length % 2 != 0) {
            throw new MicrotomeError("args array should be even in length");
        }

        for (int ii = 0; ii < args.length; ii += 2) {
            if (!(args[ii] instanceof String)) {
                throw new MicrotomeError("Each prop pair should start with a String");
            }
            map.put((String)args[ii], new PropAnnotation(args[ii + 1]));
        }
        return map;
    }

    public PropSpec (String name, Map<String, Annotation> annos, Class<?>[] valueClasses) {
        this.name = name;
        _annotations = annos == null ? Collections.<String, Annotation>emptyMap() : annos;
        this.valueType = valueClasses == null ? null : TypeInfo.fromClasses(valueClasses);
    }

    public boolean hasAnnotation (String name) {
        return _annotations.containsKey(name);
    }

    public Annotation getAnnotation (String name) {
        Annotation anno = _annotations.get(name);
        return anno == null ? NULL_ANNO : anno;
    }

    protected static class PropAnnotation implements Annotation {
        public PropAnnotation (Object value) {
            _value = value;
        }

        @Override public boolean boolValue (boolean defaultVal) {
            return _value instanceof Boolean ? (Boolean)_value : defaultVal;
        }

        @Override public int intValue (int defaultVal) {
            return _value instanceof Integer ? ((Number)_value).intValue() : defaultVal;
        }

        @Override public float numberValue (float defaultVal) {
            return _value instanceof Float ? ((Number)_value).floatValue() : defaultVal;
        }

        @Override public String stringValue (String defaultVal) {
            return _value instanceof String ? (String)_value : defaultVal;
        }

        protected final Object _value;
    }

    protected static final Annotation NULL_ANNO = new PropAnnotation(null);

    protected final Map<String, Annotation> _annotations;
}
