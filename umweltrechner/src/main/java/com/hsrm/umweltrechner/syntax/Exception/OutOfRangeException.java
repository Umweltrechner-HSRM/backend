package com.hsrm.umweltrechner.syntax.Exception;

/**
 * Thrown if a value has exceeded the permissible range for type double.
 * ({@link Double#MIN_VALUE} - {@link Double#MAX_VALUE}).
 */
public class OutOfRangeException extends InterpreterException {
    public OutOfRangeException(int lineIndex, int charIndex) {
        super(lineIndex, charIndex, "OutOfRangeException: Value has gone out of range");
    }

    public OutOfRangeException() {
        super(0, 0, "OutOfRangeException: Value out of range (must be between Double.MIN_VALUE and Double.MAX_VALUE)");
    }
}
