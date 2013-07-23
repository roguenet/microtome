package com.microtome.prop;

import com.microtome.core.Annotation;
import com.microtome.core.TypeInfo;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PropSpec {
    public String name;
    public TypeInfo valueType;

    public PropSpec (String name, Map<String, Object> annos, List<Class<?>> valueClasses) {
        this.name = name;
        _annotations = annos == null ? Collections.<String, Annotation>emptyMap() :
            new HashMap<String, Annotation>();
        if (annos != null) {
            for (Map.Entry<String, Object> anno : annos.entrySet()) {
                _annotations.put(anno.getKey(), new PropAnnotation(anno.getValue()));
            }
        }
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
            return _value instanceof Integer ? (Integer)_value : defaultVal;
        }

        @Override public float numberValue (float defaultVal) {
            return _value instanceof Float ? (Float)_value : defaultVal;
        }

        @Override public String stringValue (String defaultVal) {
            return _value instanceof String ? (String)_value : defaultVal;
        }

        protected final Object _value;
    }

    protected static final Annotation NULL_ANNO = new PropAnnotation(null);

    protected final Map<String, Annotation> _annotations;
}
