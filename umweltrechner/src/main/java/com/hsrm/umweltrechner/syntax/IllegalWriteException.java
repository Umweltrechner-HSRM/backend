package com.hsrm.umweltrechner.syntax;

public class IllegalWriteException extends InterpreterException {
    String sym;

    public IllegalWriteException(String sym) {
        super("Can't change value of read-only symbol " + sym);
        this.sym = sym;
    }
}
