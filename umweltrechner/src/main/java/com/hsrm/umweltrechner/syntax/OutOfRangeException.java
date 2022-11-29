package com.hsrm.umweltrechner.syntax;

public class OutOfRangeException extends InterpreterException {
    public OutOfRangeException(int line, int index) {
        super("Value has gone out of range at line " + (line + 1) + " and character " + (index + 1));
    }

    public OutOfRangeException() {
        super("Value out of range (must be between Double.MIN_VALUE and Double.MAX_VALUE)");
    }
}
