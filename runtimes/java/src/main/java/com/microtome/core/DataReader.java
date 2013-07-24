package com.microtome.core;

import com.microtome.error.MicrotomeError;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/** Wraps a Readableelement and provides additional convenience functions. */
public class DataReader {
    public DataReader (ReadableObject data) {
        _data = data;
    }

    public ReadableObject data () {
        return _data;
    }

    public String name () {
        return _data.name();
    }

    public boolean hasValue (String name) {
        return _data.hasValue(name);
    }

    public List<DataReader> children () {
        if (_children == null) {
            _children = new ArrayList<DataReader>();
            for (ReadableObject element : _data.children()) {
                _children.add(new DataReader(element));
            }
        }
        return _children;
    }

    public boolean hasChild (String name) {
        return (getChild(name) != null);
    }

    public DataReader getChild (String name) {
        if (_childrenByName == null) {
            _childrenByName = new HashMap<String, DataReader>();
            for (DataReader child : children()) {
                _childrenByName.put(child.name(), child);
            }
        }
        return _childrenByName.get(name);
    }

    public DataReader requireChild (String name) {
        DataReader child = getChild(name);
        if (child == null) {
            throw new MicrotomeError(_data, "Missing required child [name=" + name + "]");
        }
        return child;
    }

    public String getString (String name, String defaultVal) {
        return hasValue(name) ? _data.getString(name) : defaultVal;
    }

    public boolean getBool (String name, boolean defaultVal) {
        return hasValue(name) ? requireBool(name) : defaultVal;
    }

    public int getInt (String name, int defaultVal) {
        return hasValue(name) ? requireInt(name) : defaultVal;
    }

    public double getNumber (String name, double defaultVal) {
        return hasValue(name) ? requireNumber(name) : defaultVal;
    }

    public List<Integer> getInts (String name) {
        return getInts(name, 0, ",", new ArrayList<Integer>());
    }

    public List<Integer> getInts (String name, int count, String delim, List<Integer> defaultVal) {
        return hasValue(name) ? requireInts(name, count, delim) : defaultVal;
    }

    public List<Double> getNumbers (String name) {
        return getNumbers(name, 0, ",", new ArrayList<Double>());
    }

    public List<Double> getNumbers (String name, int count, String delim, List<Double> defaultVal) {
        return hasValue(name) ? requireNumbers(name, count, delim) : defaultVal;
    }

    public String requireString (String name) {
        return _data.getString(name);
    }

    public boolean requireBool (String name) {
        return _data.getBool(name);
    }

    public int requireInt (String name) {
        return _data.getInt(name);
    }

    public double requireNumber (String name) {
        return _data.getNumber(name);
    }

    public List<Integer> requireInts (String name, int count, String delim) {
        return requireList(name, count, delim, INT_PARSER);
    }

    public List<Double> requireNumbers (String name, int count, String delim) {
        return requireList(name, count, delim, FLOAT_PARSER);
    }

    protected <T> List<T> requireList (String name, int count, String delim, Parser<T> parser) {
        List<T> out = new ArrayList<T>();
        for (String value : requireString(name).split(delim)) {
            out.add(parser.parse(value));
        }

        if (count > 0 && out.size() != count) {
            throw new MicrotomeError(_data, "bad list length [name=" + name + ", req=" + count +
                ", got=" + out.size() + "]");
        }
        return out;
    }

    protected static interface Parser<T> {
        T parse (String value);
    }

    protected static final Parser<Integer> INT_PARSER = new Parser<Integer>() {
        @Override public Integer parse (String value) {
            try {
                return Integer.parseInt(value);
            } catch (NumberFormatException nfe) {
                throw new MicrotomeError("Value is not an int [" + value + "]");
            }
        }
    };

    protected static final Parser<Double> FLOAT_PARSER = new Parser<Double>() {
        @Override public Double parse (String value) {
            try {
                return Double.parseDouble(value);
            } catch (NumberFormatException nfe) {
                throw new MicrotomeError("Value is not a number [" + value + "]");
            }
        }
    };

    protected final ReadableObject _data;

    protected List<DataReader> _children;
    protected Map<String, DataReader> _childrenByName;
}
