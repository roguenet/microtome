package com.timconkling.microtome.json;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.timconkling.microtome.core.ReadableObject;
import com.timconkling.microtome.core.WritableObject;
import com.timconkling.microtome.error.MicrotomeError;
import java.util.ArrayList;
import java.util.List;

public class MTJsonArray extends MTJsonElement implements WritableObject {
    public MTJsonArray (String name, JsonArray value) {
        super(name);
        _value = value;
    }

    @Override public List<ReadableObject> children () {
        List<ReadableObject> children = new ArrayList<ReadableObject>();
        for (JsonElement child : _value) {
            children.add(child.isJsonArray() ? new MTJsonArray(null, (JsonArray) child) :
                (child.isJsonPrimitive() ? new MTJsonPrimitive(child) :
                    new MTJsonObject(null, (JsonObject) child)));
        }
        return children;
    }

    @Override public boolean hasValue (String name) {
        // Arrays don't have values.
        return false;
    }

    @Override public boolean getBool (String name) {
        throw new MicrotomeError("getBool not supported on JsonArray");
    }

    @Override public int getInt (String name) {
        throw new MicrotomeError("getInt not supported on JsonArray");
    }

    @Override public float getNumber (String name) {
        throw new MicrotomeError("getNumber not supported on JsonArray");
    }

    @Override public String getString (String name) {
        throw new MicrotomeError("getString not supported on JsonArray");
    }

    @Override public WritableObject addChild (String name, boolean isList) {
        JsonElement elem = isList ? new JsonArray() : new JsonObject();
        _value.add(elem);
        return isList ?
            new MTJsonArray(null, (JsonArray)elem) : new MTJsonObject(null, (JsonObject)elem);
    }

    @Override public void writeBool (String name, boolean value) {
        _value.add(new JsonPrimitive(value));
    }

    @Override public void writeInt (String name, int value) {
        _value.add(new JsonPrimitive(value));
    }

    @Override public void writeNumber (String name, float value) {
        _value.add(new JsonPrimitive(value));
    }

    @Override public void writeString (String name, String value) {
        _value.add(new JsonPrimitive(value));
    }

    protected final JsonArray _value;
}
