package com.timconkling.microtome.json;

import com.google.gson.Gson;
import com.timconkling.microtome.core.ReadableObject;

public abstract class MTJsonElement implements ReadableObject {
    public MTJsonElement (String name) {
        _name = name;
    }

    public String name () {
        return _name;
    }

    public String debugDescription () {
        return new Gson().toJson(new DebugDescription(_name));
    }

    protected static class DebugDescription {
        public String name;

        public DebugDescription (String name) {
            this.name = name;
        }
    }

    protected final String _name;
}
