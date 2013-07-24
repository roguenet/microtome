package com.microtome.core;

public interface Annotation {
    boolean boolValue (boolean defaultVal);
    int intValue (int defaultVal);
    double numberValue (double defaultVal);
    String stringValue (String defaultVal);
}
