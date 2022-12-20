package com.hsrm.umweltrechner.syntax.Exception;

/**
 * Thrown if a given name is not a valid symbol name as specified in the documentation.
 */
public class InvalidSymbolException extends InterpreterException {
    public InvalidSymbolException(String symbol, int lineIndex, int charIndex) {
        super("InvalidSymbolException: Invalid symbol " + symbol, lineIndex, charIndex);
    }
}
