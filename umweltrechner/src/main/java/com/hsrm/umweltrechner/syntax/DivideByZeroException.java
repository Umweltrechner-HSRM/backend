package com.hsrm.umweltrechner.syntax;

public class DivideByZeroException extends InterpreterException {
    public DivideByZeroException(int line, int index) {
        super("Divide by zero at line " + (line + 1) + " and character " + (index + 1));
    }
}
