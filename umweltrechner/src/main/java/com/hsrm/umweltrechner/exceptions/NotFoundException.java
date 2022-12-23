package com.hsrm.umweltrechner.exceptions;

public class NotFoundException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public NotFoundException() {
    }

    public NotFoundException(String msg) {
        super(msg);
    }

    public static void throwIt(String message) {
        if (message == null) {
            throw new NotFoundException();
        }
        throw new NotFoundException(message);
    }
}
