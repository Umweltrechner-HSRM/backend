package com.hsrm.umweltrechner.syntax.exception;

/**
 * Thrown if a read only symbol (sensor) would have been written.
 */
public class IllegalWriteException extends InterpreterException {
  String symbol;

  public IllegalWriteException(String symbol, int lineIndex, int charIndex) {
    super("IllegalWriteException: Can't change value of read-only symbol " + symbol, lineIndex,
        charIndex);
    this.symbol = symbol;
  }

  public IllegalWriteException(String symbol) {
    super("IllegalWriteException: Can't change value of read-only symbol " + symbol, -1, -1);
    this.symbol = symbol;
  }
}
