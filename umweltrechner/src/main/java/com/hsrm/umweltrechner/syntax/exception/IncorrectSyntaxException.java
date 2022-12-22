package com.hsrm.umweltrechner.syntax.exception;

/**
 * Thrown if a syntax error has been encountered by the interpreter.
 */
public class IncorrectSyntaxException extends InterpreterException {
  public IncorrectSyntaxException(String message, int lineIndex, int charIndex) {
    super("IncorrectSyntaxException: " + message, lineIndex, charIndex);
  }
}
