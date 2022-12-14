package com.hsrm.umweltrechner.syntax.Exception;

/**
 * Thrown if a syntax error has been encountered by the interpreter.
 */
public class IncorrectSyntaxException extends InterpreterException {
    public IncorrectSyntaxException(String message, int lineIndex, int charIndex) {
        super(lineIndex, charIndex, "IncorrectSyntaxException: " + message);
    }
}
