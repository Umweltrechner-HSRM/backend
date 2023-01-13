package com.hsrm.umweltrechner.exceptions.interpreter;

/**
 * Thrown if an exception has been encountered by the interpreter. As the parent class for all
 * interpreter exceptions, it provides fields for the line and character in the equations at which
 * the exception occurred. The error message can be retrieved using the {@link #getMessage()} method
 * inherited from {@link Exception}.
 */
public abstract class InterpreterException extends RuntimeException {
  private final int line;
  private final int character;

  public InterpreterException(String message, int lineIndex, int charIndex) {
    super(message + " (line " + (lineIndex + 1) + ", character " + (charIndex + 1) + ")");
    this.line = lineIndex + 1;
    this.character = charIndex + 1;
  }

  public int getLine() {
    return line;
  }

  public int getChar() {
    return character;
  }
}
