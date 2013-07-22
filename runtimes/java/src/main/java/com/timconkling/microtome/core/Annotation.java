package com.timconkling.microtome.core;

public interface Annotation {
    boolean boolValue (boolean defaultVal);
    int intValue (int defaultVal);
    float numberValue (float defaultVal);
    String stringValue (String defaultVal);
}
