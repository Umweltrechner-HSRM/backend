package com.hsrm.umweltrechner.exceptions;

public class NotAuthorizedException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public NotAuthorizedException() {
    }

    public NotAuthorizedException(String msg) {
        super(msg);
    }

    public static void throwIt(String message) {
        if (message == null) {
            throw new NotAuthorizedException();
        }
        throw new NotAuthorizedException(message);
    }
}
