package com.timconkling.microtome.json;

import com.google.gson.JsonElement;
import com.timconkling.microtome.core.ReadableObject;
import java.util.Collections;
import java.util.List;

/** Only used for JsonArray.children() */
public class MTJsonPrimitive extends MTJsonElement {
    public MTJsonPrimitive (JsonElement value) {
        super(null);
        _value = value;
    }

    @Override public List<ReadableObject> children () {
        return Collections.emptyList();
    }

    @Override public boolean hasValue (String name) {
        return name == null;
    }

    @Override public boolean getBool (String name) {
        return _value.getAsBoolean();
    }

    @Override public int getInt (String name) {
        return _value.getAsInt();
    }

    @Override public float getNumber (String name) {
        return _value.getAsFloat();
    }

    @Override public String getString (String name) {
        return _value.getAsString();
    }

    protected final JsonElement _value;
}
