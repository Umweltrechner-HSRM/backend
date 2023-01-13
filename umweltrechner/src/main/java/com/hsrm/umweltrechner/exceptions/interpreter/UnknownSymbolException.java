package com.hsrm.umweltrechner.exceptions.interpreter;

/**
 * Thrown if a given symbol does not exist as a valid key in the symbol table or if the value of a
 * symbol was read but not initialized ({@code null} value).
 */
public class UnknownSymbolException extends InterpreterException {
  String symbol;

  public UnknownSymbolException(String symbol, int lineIndex, int charIndex) {
    super("UnknownSymbolException: Symbol '" + symbol + "' is unknown or has not been " +
        "initialized", lineIndex, charIndex);
    this.symbol = symbol;
  }

  public UnknownSymbolException(String symbol) {
    super("UnknownSymbolException: Symbol '" + symbol + "' is unknown or has not been " +
        "initialized", -1, -1);
  }
}