package com.microtome;

import com.microtome.core.LibraryItem;

/**
 * A Tome is a collection of uniquely-named Pages.
 */
public interface Tome extends LibraryItem {
    /** @return the base class for Pages in the Tome. */
    Class<?> pageClass ();

    /** @return the number of Pages in the Tome */
    int length ();

    /** @return true if the Tome has a Page with the given name */
    boolean hasPage (String name);

    /** @return the Page with the given name, or null if no such page is in the Tome */
    Page getPage (String name);

    /** @return the Page with the given name. Throws an error if there is no such page. */
    Page requirePage (String name);
}
