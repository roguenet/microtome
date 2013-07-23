package com.timconkling.microtome.json;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.timconkling.microtome.core.ReadableObject;
import com.timconkling.microtome.core.WritableObject;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MTJsonObject extends MTJsonElement implements WritableObject {
    public MTJsonObject (String name, JsonObject value) {
        super(name);
        _value = value;
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
        return _value.get(name).getAsBoolean();
    }

    @Override public int getInt (String name) {
        return _value.get(name).getAsInt();
    }

    @Override public float getNumber (String name) {
        return _value.get(name).getAsFloat();
    }

    @Override public String getString (String name) {
        return _value.get(name).getAsString();
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

    @Override public void writeNumber (String name, float value) {
        _value.addProperty(name, value);
    }

    @Override public void writeString (String name, String value) {
        _value.addProperty(name, value);
    }

    protected final JsonObject _value;
}
