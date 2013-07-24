package com.microtome.core;

import java.util.regex.Pattern;

public class Defs {
    public static final String PAGE_TYPE_ATTR = "pageType";
    public static final String IS_TOME_ATTR = "isTome";
    public static final String TEMPLATE_ATTR = "template";

    public static final String NULLABLE_ANNOTATION = "nullable";
    public static final String MIN_ANNOTATION = "min";
    public static final String MAX_ANNOATATION = "max";
    public static final String DEFAULT_ANNOTATION = "default";

    public static final String NAME_SEPARATOR = ".";
    public static final Pattern NAME_SEP_PATTERN = Pattern.compile("\\.");
}
