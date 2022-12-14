package com.hsrm.umweltrechner.syntax.Exception;

/**
 * Thrown if a read only symbol (sensor) would have been written.
 */
public class IllegalWriteException extends InterpreterException {
    String symbol;

    public IllegalWriteException(String symbol, int lineIndex, int charIndex) {
        super(lineIndex, charIndex, "IllegalWriteException: Can't change value of read-only symbol " + symbol);
        this.symbol = symbol;
    }

    public IllegalWriteException(String symbol) {
        super(0, 0, "IllegalWriteException: Can't change value of read-only symbol " + symbol);
        this.symbol = symbol;
    }
}
