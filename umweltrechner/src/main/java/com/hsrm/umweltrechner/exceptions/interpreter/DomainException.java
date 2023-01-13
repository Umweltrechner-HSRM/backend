package com.hsrm.umweltrechner.exceptions.interpreter;

/**
 * Thrown if an operation would have made a value leave the domain of real numbers (no imaginary
 * numbers).
 */
public class DomainException extends InterpreterException {
  public DomainException(String exception, int lineIndex, int charIndex) {
    super("DomainException: " + exception, lineIndex, charIndex);
  }
}
