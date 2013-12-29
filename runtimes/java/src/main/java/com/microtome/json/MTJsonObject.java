package com.microtome.json;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.microtome.core.Defs;
import com.microtome.core.ReadableObject;
import com.microtome.core.WritableObject;
import com.microtome.error.MicrotomeError;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MTJsonObject extends MTJsonElement implements WritableObject {
    public MTJsonObject (String name, JsonObject value) {
        super(name);
        if (value == null) throw new MicrotomeError("Value is null! [name=" + name() + "]");
        _value = value;
    }

    public JsonObject getValue () {
        return _value;
    }

    @Override public String name () {
        if (_name != null) return _name;
        // if we have no name, default to something sensible and valid, mimicking the built-in names
        // in XML MT docs.
        if (hasValue(Defs.PAGE_TYPE_ATTR)) {
            String type = getString(Defs.PAGE_TYPE_ATTR);
            return type.substring(0, 1).toLowerCase() + type.substring(1);
        }
        return super.name();
    }

    @Override public List<ReadableObject> children () {
        List<ReadableObject> children = new ArrayList<ReadableObject>();
        for (Map.Entry<String, JsonElement> child : _value.entrySet()) {
            JsonElement value = child.getValue();
            if (value.isJsonNull()) continue;

            if (!value.isJsonPrimitive()) {
                children.add(value.isJsonArray() ?
                    new MTJsonArray(child.getKey(), (JsonArray)value) :
                    new MTJsonObject(child.getKey(), (JsonObject)value));
            }
        }
        return children;
    }

    @Override public boolean hasValue (String name) {
        JsonElement value = _value.get(name);
        return value != null && value.isJsonPrimitive();
    }

    @Override public boolean getBool (String name) {
        JsonElement elem = _value.get(name);
        if (elem == null) {
            throw new MicrotomeError("Value missing! [name=" + name + "]");
        }
        return elem.getAsBoolean();
    }

    @Override public int getInt (String name) {
        JsonElement elem = _value.get(name);
        if (elem == null) {
            throw new MicrotomeError("Value missing! [name=" + name + "]");
        }
        try {
            return elem.getAsInt();
        } catch (NumberFormatException nfe) {
            throw new MicrotomeError("Value malformed! [name=" + name + ", value=" +
                elem.getAsString() + "]");
        }
    }

    @Override public double getNumber (String name) {
        JsonElement elem = _value.get(name);
        if (elem == null) {
            throw new MicrotomeError("Value missing! [name=" + name + "]");
        }
        try {
            return elem.getAsDouble();
        } catch (NumberFormatException nfe) {
            throw new MicrotomeError("Value malformed! [name=" + name + ", value=" +
                elem.getAsString() + "]");
        }
    }

    @Override public String getString (String name) {
        JsonElement elem = _value.get(name);
        if (elem == null) {
            throw new MicrotomeError("Value missing! [name=" + name + "]");
        }
        return elem.getAsString();
    }

    @Override public WritableObject addChild (String name, boolean isList) {
        JsonElement elem = isList ? new JsonArray() : new JsonObject();
        _value.add(name, elem);
        return isList ? new MTJsonArray(name, (JsonArray)elem) :
            new MTJsonObject(name, (JsonObject)elem);
    }

    @Override public void writeBool (String name, boolean value) {
        _value.addProperty(name, value);
    }

    @Override public void writeInt (String name, int value) {
        _value.addProperty(name, value);
    }

    @Override public void writeNumber (String name, double value) {
        _value.addProperty(name, value);
    }

    @Override public void writeString (String name, String value) {
        _value.addProperty(name, value);
    }

    protected final JsonObject _value;
}
