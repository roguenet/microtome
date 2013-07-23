package com.microtome.core;

public interface LibraryItem extends MicrotomeItem {
    String qualifiedName ();
    TypeInfo typeInfo ();
    Object childNamed (String name);
}
