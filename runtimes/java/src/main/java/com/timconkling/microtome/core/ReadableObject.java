package com.timconkling.microtome.core;

import java.util.List;

public interface ReadableObject {
    String name ();
    String debugDescription ();
    List<ReadableObject> children ();

    boolean hasValue (String name);
    boolean getBool (String name);
    int getInt (String name);
    float getNumber (String name);
    String getString (String name);
}
