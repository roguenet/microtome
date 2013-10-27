package com.microtome.error;

import com.microtome.core.ReadableObject;

public class MicrotomeError extends RuntimeException {
    public MicrotomeError (String message) {
        this(null, message);
    }

    public MicrotomeError (ReadableObject badElement, String message) {
        super(badElement == null ? message : message + "\ndata: " + badElement.debugDescription());
    }
}