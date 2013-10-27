package com.microtome.util;

import com.microtome.MutablePage;
import com.microtome.core.Defs;
import com.microtome.prop.Prop;

public class Util {
    public static String pageTypeName (Class<?> pageClazz) {
        String name = pageClazz.getSimpleName();
        return name.startsWith(MUTABLE_PREFIX) ? name.substring(MUTABLE_PREFIX.length()) : name;
    }

    public static boolean validLibraryItemName (String name) {
        // library items cannot have '.' in the name
        return name != null && name.length() > 0 && !name.contains(Defs.NAME_SEPARATOR);
    }

    public static Prop getProp (MutablePage page, String name) {
        for (Prop prop : page.props()) {
            if (prop.name().equals(name)) {
                return prop;
            }
        }
        return null;
    }

    protected static final String MUTABLE_PREFIX = "Mutable";
}