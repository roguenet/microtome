package com.timconkling.microtome.core;

import com.timconkling.microtome.Library;
import java.util.List;

/** The common interface for LibraryItems and the Library itself. */
public interface MicrotomeItem {
    String name ();
    Library library ();
    MicrotomeItem parent ();
    List<LibraryItem> children ();
}
