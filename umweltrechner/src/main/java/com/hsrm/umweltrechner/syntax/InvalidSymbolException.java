package com.hsrm.umweltrechner.syntax;

public class InvalidSymbolException extends InterpreterException {
    public InvalidSymbolException(String sym) {
        super("Invalid symbol " + sym);
    }
}
