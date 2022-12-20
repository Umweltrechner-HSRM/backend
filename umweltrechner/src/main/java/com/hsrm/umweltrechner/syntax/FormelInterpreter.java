package com.hsrm.umweltrechner.syntax;

import com.hsrm.umweltrechner.syntax.Exception.*;

import java.util.*;
import java.util.AbstractMap.SimpleEntry;

import org.springframework.stereotype.Component;

import lombok.Data;

@Component("FormelInterpreter")

public class FormelInterpreter implements Interpreter {

    @Data
    private char[][] equations;
    private int lineIndex = 0;
    private int charIndex = 0;
    private long startTime;
    private long variableTime;
    private boolean syntaxOnly = false;
    private final SymbolTable table = new SymbolTable();

    // Only to be used internally as part of the syntax validation
    private FormelInterpreter(String equations, boolean syntaxOnly) {
        equations = equations + '\n';
        this.equations = createCharArray(removeComments(equations));
        this.syntaxOnly = syntaxOnly;
    }

    public FormelInterpreter() {
        this.equations = new char[1]['\n'];
    }

    //// Helper methods ////

    // Get next character
    private char next() {
        return equations[lineIndex][++charIndex];
    }

    // Get current character
    private char read() {
        return equations[lineIndex][charIndex];
    }

    private char skipSpaces() {
        char ch = read();
        while (ch == ' ') ch = next();
        return ch;
    }

    private boolean isLetter(char ch) {
        // A-Z, a-z, underscore
        return Character.isLetter(ch) || ch == '_';
    }

    private boolean isDigit(char ch) {
        return Character.isDigit(ch);
    }

    private boolean isRelationalOperator(char ch) {
        char[] operators = {'=', '!', '<', '>'};
        for (char c : operators) {
            if (ch == c) return true;
        }
        return false;
    }

    private boolean isAssignmentOperator(char ch) {
        char[] operators = {'=', '+', '-', '*', '/'};
        for (char c : operators) {
            if (ch == c) return true;
        }
        return false;
    }

    // Split the input string into lines with a guaranteed newline character at the end
    private char[][] createCharArray(String input) {
        String[] lines = input.split("\n");
        char[][] result = new char[lines.length][];

        for (int i = 0; i < lines.length; i++) {
            result[i] = (lines[i] + "\n").toCharArray();
        }

        return result;
    }

    private String removeComments(String input) {
        char[] chars = (input + '\n').toCharArray();
        ArrayList<Character> buffer = new ArrayList<>();

        // Omit all characters between // (inclusive) and \n (exclusive)
        for (int i = 0; i < chars.length - 1; i++) {
            if (chars[i] == '/' && chars[i + 1] == '/') {
                while (chars[i] != '\n') i++;
            }
            buffer.add(chars[i]);
        }

        // Collect the remaining characters from the buffer for concatenation
        char[] result = new char[buffer.size()];
        for (int i = 0; i < result.length; i++) {
            result[i] = buffer.get(i);
        }

        return new String(result);
    }

    //// Interface methods ////

    // Set the equations to be used by the interpreter
    // Since the program is constantly looking ahead one character, adding a newline
    // character ensures that there is at least one character left to read
    public void setEquations(String newEquations) {

        // checkSyntax(newEquations);
        newEquations = newEquations + '\n';
        equations = createCharArray(removeComments(newEquations));
    }

    // Check that the syntax is valid by doing one calculation run with a second interpreter instance
    public void checkSyntax(String newEquations) throws IncorrectSyntaxException,
            UnknownSymbolException, IllegalWriteException, DivideByZeroException, OutOfRangeException, DomainException {

        FormelInterpreter tester = new FormelInterpreter(newEquations, true);
        for (Map.Entry<String, SymbolTable.SymbolEntry> entry : table.symbols.entrySet()) {
            try {
                tester.table.setSymbol(entry.getKey(), entry.getValue());
            } catch (IllegalWriteException e) {
                // this should never happen
            }
        }
        tester.calculate();
    }

    // Only to be used by surrounding application to add new read-only variables (sensors)
    public void addSensor(String symbol, double value, long timestamp) throws OutOfRangeException,
            InvalidSymbolException {

        if (value == Double.MIN_VALUE || value == Double.MAX_VALUE) throw new OutOfRangeException();

        char[] charArray = symbol.toCharArray();

        if (charArray.length == 0) throw new InvalidSymbolException(symbol, lineIndex, charIndex);

        for (int i = 0; i < charArray.length; i++) {
            if (!(isLetter(charArray[i]) || isDigit(charArray[i])))
                throw new InvalidSymbolException(symbol, lineIndex, charIndex);
            if (i == 0 && isDigit(charArray[i]))
                throw new InvalidSymbolException(symbol, lineIndex, charIndex);
        }

        SymbolTable.SymbolEntry entry = new SymbolTable.SymbolEntry(value, true, timestamp);
        table.addSensor(symbol, entry);
    }

    public void addSensor(String symbol, double value) throws OutOfRangeException, InvalidSymbolException {
        addSensor(symbol, value, System.currentTimeMillis());
    }

    public void removeSymbol(String symbol) throws UnknownSymbolException {
        try {
            table.removeSymbol(symbol);
        } catch (UnknownSymbolException e) {
            throw new UnknownSymbolException(e.getMessage(), lineIndex, charIndex);
        }
    }

    public boolean symbolExists(String symbol) {
        return table.symbolExists(symbol);
    }

    /**
     * @deprecated method signature has been replaced by {@link #removeSymbol(String)}
     */
    @Deprecated
    public void removeSensor(String sensor) throws UnknownSymbolException {
        removeSymbol(sensor);
    }

    /**
     * @deprecated method signature has been replaced by {@link #symbolExists(String)}
     */
    @Deprecated
    public boolean sensorExists(String sensor) {
        return symbolExists(sensor);
    }

    // Only get key-value pairs of variable name and value, without description or flags
    public HashMap<String, Double> getVariables() {
        return table.getVariables();
    }

    // Get key-value pairs of variable name, value and flags
    public HashMap<String, SymbolTable.SymbolEntry> getVariablesWithFlag() {
        return table.getVariablesWithFlag();
    }

    public Double getVariable(String symbol) throws UnknownSymbolException {
        try {
            return table.getVariable(symbol);
        } catch (UnknownSymbolException e) {
            throw new UnknownSymbolException(e.getMessage(), lineIndex, charIndex);
        }
    }

    public void clearSymbolTable() {
        table.clearSymbolTable();
    }

    public void clearVariables() {
        table.clearVariables();
    }

    // Run the equations through the interpreter using the variables present in the HashMap
    public void calculate() throws IncorrectSyntaxException, UnknownSymbolException,
            IllegalWriteException, DivideByZeroException, OutOfRangeException, DomainException {

        startTime = System.currentTimeMillis();

        // Iterate through all lines
        for (lineIndex = 0; lineIndex < equations.length; lineIndex++) {
            charIndex = 0;

            // Skip any spaces or newlines
            char ch = skipSpaces();
            if (ch == '\n') continue;

            // Check first (real) character of line, then proceed accordingly
            // The remaining characters are iterated over in the called methods

            // Labels are ignored as they only matter for goto statements which are handled separately
            if (ch == ':') continue;
            if (isLetter(ch)) {
                // If the word is a keyword, proceed accordingly and start the next line afterwards
                if (keyword(ch)) continue;
                ch = equation(ch);
                // Throw a syntax error if any unexpected characters come after the statement
                if (ch != '\n')
                    throw new IncorrectSyntaxException("Unexpected character '"+ch+"'", lineIndex, charIndex);
            } else
                throw new IncorrectSyntaxException("Unexpected character '"+ch+"'", lineIndex, charIndex);
        }
    }

    //// Interpreter methods ////

    // Returns true if the line started with a keyword and calls the corresponding keyword method
    private boolean keyword(char ch) throws IncorrectSyntaxException, UnknownSymbolException,
            IllegalWriteException, DivideByZeroException, OutOfRangeException, DomainException {

        int startIndex = charIndex;

        // Build potential keyword from letters
        StringBuilder symbol = new StringBuilder();
        while (isLetter(ch)) {
            symbol.append(ch);
            ch = next();
        }
        ch = skipSpaces();

        // Jump to beginning of line and return false if not a keyword
        switch (symbol.toString()) {
            case "if" -> {
                ifStatement(ch);
                return true;
            }
            case "goto" -> {
                gotoLabel(ch);
                return true;
            }
            default -> {
                charIndex = startIndex;
                return false;
            }
        }
    }

    private void ifStatement(char ch) throws IncorrectSyntaxException, UnknownSymbolException,
            IllegalWriteException, DivideByZeroException, OutOfRangeException, DomainException {

        boolean conditionTrue;
        if (ch == '(') {
            next();
            ch = skipSpaces();
            conditionTrue = condition(ch);
            ch = read();
            if (ch == ')') next();
            else
                throw new IncorrectSyntaxException("Expected ')' but got '"+ch+"'", lineIndex, charIndex);
        } else {
            conditionTrue = condition(ch);
        }

        ch = skipSpaces();

        // If statements are always followed by a goto statement
        StringBuilder symbol = new StringBuilder();
        while (isLetter(ch)) {
            symbol.append(ch);
            ch = next();
        }
        ch = skipSpaces();

        if (!symbol.toString().equals("goto"))
            throw new IncorrectSyntaxException("Expected 'goto' but got '"+symbol+"'", lineIndex, charIndex);

        // Only execute goto statement if comparison returned true
        if (conditionTrue) gotoLabel(ch);
    }

    // Only here for clarity or future expansion
    private boolean condition(char ch) throws IncorrectSyntaxException,
            UnknownSymbolException, DivideByZeroException, OutOfRangeException, DomainException, IllegalWriteException {

        return disjunction(ch);
    }

    // Disjunction consists of one or multiple conjunctions concatenated by logical OR-operators
    private boolean disjunction(char ch) throws IncorrectSyntaxException,
            UnknownSymbolException, DivideByZeroException, OutOfRangeException, DomainException, IllegalWriteException {

        boolean result = conjunction(ch);

        // Interpret logical OR-operator
        ch = read();
        while (ch == '|') {
            ch = next();
            if (ch == '|') {
                next();
                ch = skipSpaces();
                result = conjunction(ch) || result;
                ch = read();
            } else
                throw new IncorrectSyntaxException("Expected '||' but got '|"+ch+"'", lineIndex, charIndex);
        }
        skipSpaces();

        return result;
    }

    // Conjunction consists of one or multiple bools concatenated by logical AND-operators
    private boolean conjunction(char ch) throws IncorrectSyntaxException,
            UnknownSymbolException, DivideByZeroException, OutOfRangeException, DomainException, IllegalWriteException {

        boolean result = bool(ch);

        // Interpret logical AND-operator
        ch = read();
        while (ch == '&') {
            ch = next();
            if (ch == '&') {
                next();
                ch = skipSpaces();
                result = result && bool(ch);
                ch = read();
            } else
                throw new IncorrectSyntaxException("Expected '&&' but got '&"+ch+"'", lineIndex, charIndex);
        }
        skipSpaces();

        return result;
    }

    // Bool can be either a comparison or an enclosed disjunction
    private boolean bool(char ch) throws IncorrectSyntaxException,
            UnknownSymbolException, DivideByZeroException, OutOfRangeException, DomainException, IllegalWriteException {
        // Interpret NOT-operator before bool
        boolean negation = false;

        if (ch == '!') {
            negation = true;
            next();
            ch = skipSpaces();
        }

        boolean result;

        // If the first character is an opening parenthesis, it's an enclosed disjunction
        // otherwise it's a comparison
        if (ch == '(') {
            next();
            ch = skipSpaces();
            result = disjunction(ch);
            ch = read();
            if (ch == ')') next();
            else
                throw new IncorrectSyntaxException("Expected ')' but got '"+ch+"'", lineIndex, charIndex);
        } else
            result = comparison(ch);

        skipSpaces();

        if (negation)
            result = !result;

        return result;
    }

    // Compare two expressions using a relational operator
    private boolean comparison(char ch) throws IncorrectSyntaxException, UnknownSymbolException,
            DivideByZeroException, OutOfRangeException, DomainException {
        double value1 = expression(ch);
        ch = read();

        StringBuilder operator = new StringBuilder();
        while (isRelationalOperator(ch)) {
            operator.append(ch);
            ch = next();
        }
        ch = skipSpaces();

        // Perform comparison depending on read relational operator
        double value2 = expression(ch);
        return switch (operator.toString()) {
            case "==" -> (value1 == value2);
            case "!=" -> (value1 != value2);
            case "<" -> (value1 < value2);
            case "<=" -> (value1 <= value2);
            case ">=" -> (value1 >= value2);
            case ">" -> (value1 > value2);
            default -> throw new IncorrectSyntaxException("Unknown operator '"+operator+"'", lineIndex, charIndex);
        };
    }

    private void gotoLabel(char ch) throws IncorrectSyntaxException, UnknownSymbolException {
        StringBuilder expectedLabel = new StringBuilder();
        while (isLetter(ch) || isDigit(ch)) {
            expectedLabel.append(ch);
            ch = next();
        }
        ch = skipSpaces();

        // Throw a syntax error if any unexpected characters come after the statement
        if (ch != '\n')
            throw new IncorrectSyntaxException("Unexpected character '"+ch+"'", lineIndex, charIndex);

        int currentLine = lineIndex;
        int currentIndex = charIndex;

        // Iterate over each line, looking for the label at the beginning (preceded by a colon)
        while (lineIndex < equations.length - 1) {
            // Go to the next line and start at the first character
            lineIndex++;
            charIndex = 0;

            ch = skipSpaces();

            // If the first real character is a colon, it's a label
            if (ch == ':') {
                ch = next();
                StringBuilder foundLabel = new StringBuilder();
                while (isLetter(ch) || isDigit(ch)) {
                    foundLabel.append(ch);
                    ch = next();
                }
                ch = skipSpaces();

                // Throw a syntax error if any unexpected characters come after the statement
                if (ch != '\n')
                    throw new IncorrectSyntaxException("Unexpected character '"+ch+"'", lineIndex, charIndex);

                // If the label is the label we are looking for, stop the search here
                // Otherwise continue on to the next line
                if (expectedLabel.toString().equals(foundLabel.toString())) {
                    if (syntaxOnly) {
                        lineIndex = currentLine;
                        charIndex = currentIndex;
                    }
                    return;
                }
            }
        }

        // Label could not be found
        throw new UnknownSymbolException(expectedLabel.toString(), currentLine, currentIndex);
    }

    // Only here for clarity or future expansion
    private char equation(char ch) throws IncorrectSyntaxException, UnknownSymbolException,
            IllegalWriteException, DivideByZeroException, OutOfRangeException, DomainException {
        return assignmentStatement(ch);
    }

    // Assign a value to a variable in the HashMap
    // Syntax: symbol := expression
    private char assignmentStatement(char ch) throws IncorrectSyntaxException,
            UnknownSymbolException, IllegalWriteException, DivideByZeroException, OutOfRangeException, DomainException {

        SimpleEntry<String, Double> var = variable(ch);
        ch = read();

        variableTime = 0;
        int startIndex = charIndex;
        double result;

        try {
            result = assignmentOperator(ch, var.getValue());
            ch = read();

        } catch (NullPointerException e) {
            // If the variable was previously undefined, it's value is null
            charIndex = startIndex;
            throw new UnknownSymbolException(var.getKey(), lineIndex, charIndex);
        }

        if (variableTime == 0)
            variableTime = startTime;

        // Update the symbol in the table, forward Exception with position in equation if thrown in SymbolTable class
        try {
            table.setSymbol(var.getKey(), new SymbolTable.SymbolEntry(result, false, variableTime));
        } catch (IllegalWriteException e) {
            throw new IllegalWriteException(e.getMessage(), lineIndex, charIndex);
        }

        return ch;
    }

    private double assignmentOperator(char ch, Double result) throws DivideByZeroException,
            DomainException, UnknownSymbolException, IncorrectSyntaxException, OutOfRangeException {

        // Read the entire operator
        StringBuilder operator = new StringBuilder();
        while (isAssignmentOperator(ch)) {
            operator.append(ch);
            ch = next();
        }
        ch = skipSpaces();

        switch (operator.toString()) {
            case "=" -> result = expression(ch);
            case "+=" -> result += expression(ch);
            case "-=" -> result -= expression(ch);
            case "*=" -> result *= expression(ch);
            case "/=" -> result /= expression(ch);
            default -> throw new IncorrectSyntaxException("Invalid assignment operator", lineIndex, charIndex);
        }

        return result;
    }

    // Build variable name, add to HashMap and retrieve value (null if previously undefined)
    private SimpleEntry<String, Double> variable(char ch) throws IncorrectSyntaxException {
        if (!isLetter(ch))
            throw new IncorrectSyntaxException(
                    "Variable identifier must start with a letter or an underscore", lineIndex, charIndex
            );

        // Build variable name from read characters
        StringBuilder symbol = new StringBuilder();
        while (isLetter(ch) || isDigit(ch)) {
            symbol.append(ch);
            ch = next();
        }
        skipSpaces();

        SymbolTable.SymbolEntry entry = table.getSymbol(symbol.toString());
        Double value = entry.value;

        if (entry.lastModified > variableTime)
            variableTime = entry.lastModified;

        return new SimpleEntry<>(symbol.toString(), value);
    }

    // Only here for clarity or future expansion
    private double expression(char ch) throws IncorrectSyntaxException,
            UnknownSymbolException, DivideByZeroException, OutOfRangeException, DomainException {

        return simpleExpression(ch);
    }

    // Simple expression consists of one or multiple terms concatenated by adding operators
    private double simpleExpression(char ch) throws IncorrectSyntaxException,
            UnknownSymbolException, DivideByZeroException, OutOfRangeException, DomainException {

        double result = term(ch);
        ch = read();

        // Interpret +/- operators used for addition/subtraction
        while (ch == '+' || ch == '-') {
            result = addingOperator(ch, result);
            ch = read();
        }
        skipSpaces();

        return result;
    }

    // Apply adding operator to first simple expression and second simple expression
    private double addingOperator(char ch, double result) throws IncorrectSyntaxException,
            UnknownSymbolException, DivideByZeroException, OutOfRangeException, DomainException {

        char operator = ch;
        next();
        ch = skipSpaces();
        if (operator == '+') result = result + term(ch);
        else result = result - term(ch);

        if (result == Double.MIN_VALUE || result == Double.MAX_VALUE)
            throw new OutOfRangeException(
                    "Value exceeded permissible range (number too big or too small)", lineIndex, charIndex
            );

        return result;
    }

    // Term consists of one or multiple factors concatenated by multiplying operators
    private double term(char ch) throws IncorrectSyntaxException,
            UnknownSymbolException, DivideByZeroException, OutOfRangeException, DomainException {

        double result = factor(ch);
        ch = read();

        // Interpret * or / operator
        while (ch == '*' || ch == '/') {
            result = multiplyingOperator(ch, result);
            ch = read();
        }
        skipSpaces();

        return result;
    }

    // Apply multiplying operator to first factor and new term
    private double multiplyingOperator(char ch, double result) throws IncorrectSyntaxException,
            UnknownSymbolException, DivideByZeroException, OutOfRangeException, DomainException {

        char operator = ch;
        ch = next();
        if (operator == '*') {
            if (ch == '*') {
                next();
                ch = skipSpaces();
                double fact = factor(ch);
                if (result < 0 && fact % 1 != 0)
                    throw new DomainException("Root of negative number", lineIndex, charIndex);
                result = Math.pow(result, fact);
            } else {
                ch = skipSpaces();
                result = result * factor(ch);
            }
        }
        else {
            ch = skipSpaces();
            double divisor = factor(ch);
            if (divisor == 0) throw new DivideByZeroException(lineIndex, charIndex);
            result = result / divisor;
        }

        if (result == Double.MIN_VALUE || result == Double.MAX_VALUE)
            throw new OutOfRangeException(
                    "Value exceeded permissible range (number too big or too small)", lineIndex, charIndex
            );

        return result;
    }

    // Factor can be either a variable, a method, an unsigned constant or an enclosed expression
    private double factor(char ch) throws IncorrectSyntaxException,
            UnknownSymbolException, DivideByZeroException, OutOfRangeException, DomainException {

        // Interpret for +/- sign before term
        double sign = 1;
        if (ch == '+' || ch == '-') {
            sign = sign(ch);
            ch = read();
        }

        double result;

        // If the first character is a letter, it's either a method or a variable
        if (isLetter(ch)) {
            int currentIndex = charIndex;

            // Check if symbol is method or variable
            // The symbol name is gathered in variable() or method()
            while (isLetter(ch) || isDigit(ch)) {
                ch = next();
            }
            // If the symbol is followed by opening parenthesis, it's a method
            // Otherwise it's a variable
            if (ch == '(') {
                charIndex = currentIndex;
                ch = read();

                result = method(ch);
            } else {
                charIndex = currentIndex;
                ch = read();

                SimpleEntry<String, Double> var = variable(ch);
                if (var.getValue() == null) throw new UnknownSymbolException(var.getKey(), lineIndex, charIndex);
                result = var.getValue();
            }
        }

        // If the first character is a digit, it's a number
        else if (isDigit(ch)) result = unsignedConstant(ch);

        // If the first character is an opening parenthesis, it's an enclosed expression
        else if (ch == '(') {
            next();
            ch = skipSpaces();
            result = expression(ch);
            ch = read();
            if (ch == ')') next();
            else
                throw new IncorrectSyntaxException("Expected ')' but got '"+ch+"'", lineIndex, charIndex);
        }

        // If the first character is a line, it's an enclosed positive expression
        else if (ch == '|') {
            next();
            ch = skipSpaces();
            result = Math.abs(expression(ch));
            ch = read();
            if (ch == '|') next();
            else
                throw new IncorrectSyntaxException("Expected '|' but got '"+ch+"'", lineIndex, charIndex);
        }
        else
            throw new IncorrectSyntaxException("Unexpected character '"+ch+"'", lineIndex, charIndex);

        skipSpaces();

        result = sign * result;

        if (result == Double.MIN_VALUE || result == Double.MAX_VALUE)
            throw new OutOfRangeException(
                    "Value exceeded permissible range (number too big or too small)", lineIndex, charIndex
            );

        return result;
    }

    private double sign(char ch) {
        double result;
        if (ch == '+') result = 1;
        else result = -1;

        next();
        skipSpaces();

        return result;
    }

    private double method(char ch) throws IncorrectSyntaxException,
            DivideByZeroException, DomainException, UnknownSymbolException, OutOfRangeException {

        // Read symbol name
        StringBuilder symbol = new StringBuilder();
        while (isLetter(ch) || isDigit(ch)) {
            symbol.append(ch);
            ch = next();
        }

        ch = skipSpaces();

        // read opening parenthesis
        if (ch != '(')
            throw new IncorrectSyntaxException("Expected '(' but got '"+ch+"'", lineIndex, charIndex);

        next();
        ch = skipSpaces();

        // List of read parameters
        ArrayList<Double> parameters = new ArrayList<>();

        if (ch != ')') {
            parameters.add(expression(ch));
            ch = read();
            while (ch == ',') {
                next();
                ch = skipSpaces();
                parameters.add(expression(ch));
                ch = read();
            }
        }
        if (ch != ')')
            throw new IncorrectSyntaxException("Expected ')' but got '"+ch+"'", lineIndex, charIndex);

        next();
        skipSpaces();

        // Call the corresponding method
        if (parameters.size() == 0) {
            return switch (symbol.toString()) {
                case "pi" -> Math.PI;
                case "e" -> Math.E;
                default -> throw new UnknownSymbolException(symbol.toString(), lineIndex, charIndex);
            };
        }

        if (parameters.size() == 1) {
            switch (symbol.toString()) {
                case "sin" -> { return Math.sin(parameters.get(0)); }
                case "cos" -> { return Math.cos(parameters.get(0)); }
                // tan: infinity for -90/90 degrees or -PI/2 or PI/2 radians
                case "tan" -> {
                    if (Math.abs(parameters.get(0)) == Math.PI/2)
                        throw new OutOfRangeException("Value x for tan must be -PI/2 < x < PI/2", lineIndex, charIndex);
                    return Math.tan(parameters.get(0));
                }
                // asin: param must be -1 <= x <= 1
                case "asin" -> {
                    if (Math.abs(parameters.get(0)) > 1)
                        throw new OutOfRangeException("Value x for asin must be -1 <= x <= 1", lineIndex, charIndex);
                    return Math.asin(parameters.get(0));
                }
                // acos: param must be -1 <= x <= 1
                case "acos" -> {
                    if (Math.abs(parameters.get(0)) > 1)
                        throw new OutOfRangeException("Value x for acos must be -1 <= x <= 1", lineIndex, charIndex);
                    return Math.acos(parameters.get(0));
                }
                case "atan" -> { return Math.atan(parameters.get(0)); }
                case "rad" -> { return Math.toRadians(parameters.get(0)); }
                case "deg" ->  { return Math.toDegrees(parameters.get(0)); }
                case "sqrt" -> {
                    if (parameters.get(0) < 0)
                        throw new DomainException("Root of negative number", lineIndex, charIndex);
                    return Math.sqrt(parameters.get(0));
                }
                case "abs" -> { return Math.abs(parameters.get(0)); }
                case "floor" -> { return Math.floor(parameters.get(0)); }
                case "ceil" -> { return Math.ceil(parameters.get(0)); }
                case "ln" -> { return Math.log(parameters.get(0)); } // log e
                case "log" -> { return Math.log10(parameters.get(0)); } // log 10
                default -> throw new UnknownSymbolException(symbol.toString(), lineIndex, charIndex);
            }
        }

        else if (parameters.size() == 2) {
            return switch (symbol.toString()) {
                case "min" -> Math.min(parameters.get(0), parameters.get(1));
                case "max" -> Math.max(parameters.get(0), parameters.get(1));
                default -> throw new UnknownSymbolException(symbol.toString(), lineIndex, charIndex);
            };
        }

        else
            throw new IncorrectSyntaxException(
                    "Invalid method identifier '"+symbol+"' or number of parameters '"+parameters.size()+"'",
                    lineIndex, charIndex
            );
    }

    private double unsignedConstant(char ch) {
        StringBuilder result = new StringBuilder();

        // Build number from digits
        while (isDigit(ch)) {
            result.append(ch);
            ch = next();
        }

        // Allow decimals, separated by a period or a comma
        if (ch == '.') {
            result.append('.');
            ch = next();
            while (isDigit(ch)) {
                result.append(ch);
                ch = next();
            }
        }
        skipSpaces();

        // Value range is checked in parent method, as it may be dependent on the preceding sign
        return Double.parseDouble(result.toString());
    }
}
