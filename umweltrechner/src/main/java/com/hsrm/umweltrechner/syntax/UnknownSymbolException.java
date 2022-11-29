package com.hsrm.umweltrechner.syntax;

public class UnknownSymbolException extends InterpreterException {
    String sym;

    public UnknownSymbolException(String sym) {
        super("Unknown symbol " + sym);
        this.sym = sym;
    }
}