package com.hsrm.umweltrechner.syntax.Exception;

/**
 * Thrown if a value has exceeded the permissible range for type double.
 * ({@link Double#MIN_VALUE} - {@link Double#MAX_VALUE}).
 */
public class OutOfRangeException extends InterpreterException {
    public OutOfRangeException(String exception, int lineIndex, int charIndex) {
        super("OutOfRangeException: " + exception, lineIndex, charIndex);
    }

    public OutOfRangeException() {
        super("OutOfRangeException: Value out of range (must be between Double.MIN_VALUE and Double.MAX_VALUE)", -1, -1);
    }
}
