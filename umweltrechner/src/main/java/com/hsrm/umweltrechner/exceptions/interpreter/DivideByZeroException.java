package com.hsrm.umweltrechner.exceptions.interpreter;

/**
 * Thrown if a value would have been divided by zero.
 */
public class DivideByZeroException extends InterpreterException {
  public DivideByZeroException(int lineIndex, int charIndex) {
    super("DivideByZeroException: Division by zero", lineIndex, charIndex);
  }
}
