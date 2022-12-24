package com.hsrm.umweltrechner.exceptions;

public class NotImplementedException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public NotImplementedException() {
    }

    public NotImplementedException(String msg) {
        super(msg);
    }

    public static void throwIt(String message) {
        if (message == null) {
            throw new NotImplementedException();
        }
        throw new NotImplementedException(message);
    }
}
