package com.hsrm.umweltrechner.syntax;

public class IncorrectSyntaxException extends InterpreterException {
    int line;
    int ch;

    public IncorrectSyntaxException(int line, int index) {
        super("Incorrect syntax at line " + (line + 1) + " and character " + (index + 1));
        this.line = line + 1;
        this.ch = index + 1;
    }
}
