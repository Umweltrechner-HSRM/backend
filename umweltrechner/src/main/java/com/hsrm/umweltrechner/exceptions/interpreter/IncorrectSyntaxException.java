package com.hsrm.umweltrechner.exceptions.interpreter;

/**
 * Thrown if a syntax error has been encountered by the interpreter.
 */
public class IncorrectSyntaxException extends InterpreterException {
  public IncorrectSyntaxException(String message, int lineIndex, int charIndex) {
    super("IncorrectSyntaxException: " + message, lineIndex, charIndex);
  }
}
