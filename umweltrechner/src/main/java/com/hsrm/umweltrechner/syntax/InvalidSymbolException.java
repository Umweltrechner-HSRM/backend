package com.hsrm.umweltrechner.syntax;

public class InvalidSymbolException extends Exception {
    public InvalidSymbolException(String sym) {
        super("Invalid symbol " + sym);
    }
}
