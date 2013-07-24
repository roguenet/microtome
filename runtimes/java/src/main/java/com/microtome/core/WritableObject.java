package com.microtome.core;

public interface WritableObject {
    WritableObject addChild (String name, boolean isList);

    void writeBool (String name, boolean value);
    void writeInt (String name, int value);
    void writeNumber (String name, double value);
    void writeString (String name, String value);
}
