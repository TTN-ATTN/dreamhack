package com.fasterxml.jackson.core;

import java.io.IOException;

/* loaded from: free-market-1.0.0.jar:BOOT-INF/lib/jackson-core-2.13.5.jar:com/fasterxml/jackson/core/JacksonException.class */
public abstract class JacksonException extends IOException {
    private static final long serialVersionUID = 123;

    public abstract JsonLocation getLocation();

    public abstract String getOriginalMessage();

    public abstract Object getProcessor();

    protected JacksonException(String msg) {
        super(msg);
    }

    protected JacksonException(Throwable t) {
        super(t);
    }

    protected JacksonException(String msg, Throwable rootCause) {
        super(msg, rootCause);
    }
}
