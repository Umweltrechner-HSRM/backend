package com.hsrm.umweltrechner.exceptions;

public class NotModifiedException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public NotModifiedException() {
    }

    public NotModifiedException(String msg) {
        super(msg);
    }

}
