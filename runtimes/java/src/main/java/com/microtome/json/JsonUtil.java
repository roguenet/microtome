package com.microtome.json;

import com.google.gson.JsonObject;
import com.microtome.core.ReadableObject;
import com.microtome.core.WritableObject;
import java.util.ArrayList;
import java.util.List;

public class JsonUtil {
    /** Creates ReadableObjects from a List of JsonObjects */
    public static List<ReadableObject> createReaders (List<JsonObject> jsons) {
        List<ReadableObject> readers = new ArrayList<ReadableObject>();
        for (JsonObject json : jsons) {
            readers.add(new MTJsonObject("microtome", json));
        }
        return readers;
    }

    public static WritableObject createWriter (String name, JsonObject json) {
        return new MTJsonObject(name, json);
    }
}
