package com.hsrm.umweltrechner.syntax;

import java.util.*;
import java.util.AbstractMap.SimpleEntry;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Component;

import lombok.Data;

@Component("FormelInterpreter")

public class FormelInterpreter implements Interpreter {

    @Data
    public static class SymbolEntry {
        Double value;
        boolean readOnly;
        long lastModified;

        public SymbolEntry(Double value, boolean readOnly, long lastModified) {
            this.value = value;
            this.readOnly = readOnly;
            this.lastModified = lastModified;
        }

        public SymbolEntry(Double value, boolean readOnly) {
            this(value, readOnly, System.currentTimeMillis());
        }

        public SymbolEntry(Double value) {
            this(value, false);
        }

        @Override
        public String toString() {
            return "{value=" + value + ", readOnly=" + readOnly + ", lastModified="+ lastModified + "}";
        }
    }

    private char[][] equations;
    private int line = 0;
    private int index = 0;
    private long currentTime;
    private long variableTime;
    private boolean syntax_only = false;
    private final ConcurrentHashMap<String, SymbolEntry> variables = new ConcurrentHashMap<>();

    // Only to be used internally as part of the syntax validation
    private FormelInterpreter(String equations, boolean syntax_only) {
        equations = equations + '\n';
        this.equations = setup(removeComments(equations));
        this.syntax_only = syntax_only;
    }

    public FormelInterpreter() {
        this.equations = new char[1]['\n'];
    }

    // Get next character
    private char next() {
        return equations[line][++index];
    }

    // Get current character
    private char read() {
        return equations[line][index];
    }

    private boolean isLetter(char ch) {
        // A-Z, a-z, underscore
        return Character.isLetter(ch) || ch == '_';
    }

    private boolean isDigit(char ch) {
        return Character.isDigit(ch);
    }

    private boolean isOperator(char ch) {
        char[] operators = {'=', '!', '<', '>'};
        for (char c : operators) {
            if (ch == c) return true;
        }
        return false;
    }

    // Split the input string into lines with a guaranteed newline character at the end
    private char[][] setup(String input) {
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

    // Get symbol value from variables HashMap
    private SymbolEntry getSymbol(String sym) {
        if (!variables.containsKey(sym)) {
            SymbolEntry entry = new SymbolEntry(null);
            variables.put(sym, entry);
            return entry;
        }
        return variables.get(sym);
    }

    // Add new symbol to variable HashMap, value initialized as null by default
    private SymbolEntry setSymbol(String sym, SymbolEntry entry) throws IllegalWriteException {
        if (variables.containsKey(sym) && variables.get(sym).readOnly)
            throw new IllegalWriteException(sym);
        variables.put(sym, entry);
        return entry;
    }

    // Set the equations to be used by the interpreter
    // Since the program is constantly looking ahead one character, adding a newline
    // character ensures that there is at least one character left to read
    public void setEquations(String newEquations) throws IncorrectSyntaxException,
            UnknownSymbolException, IllegalWriteException {
        // checkSyntax(newEquations);
        newEquations = newEquations + '\n';
        equations = setup(removeComments(newEquations));
    }

    // Check that the syntax is valid by doing one calculation run with a second interpreter instance
    public void checkSyntax(String newEquations) throws IncorrectSyntaxException,
            UnknownSymbolException, IllegalWriteException, DivideByZeroException, OutOfRangeException, DomainException {
        FormelInterpreter tester = new FormelInterpreter(newEquations, true);
        for (Map.Entry<String, SymbolEntry> entry : variables.entrySet()) {
            try {
                tester.setSymbol(entry.getKey(), entry.getValue());
            } catch (IllegalWriteException e) {
                // this should never happen
            }
        }
        tester.calculate();
    }

    public void setSensors(HashMap<String, Double> newSensors) throws OutOfRangeException,
            InvalidSymbolException {
        // (optional) TODO
    }

    // Only to be used by surrounding application to add new read-only variables (sensors)
    public void addSensor(String name, double value, long timestamp) throws OutOfRangeException,
            InvalidSymbolException {

        if (value == Double.MIN_VALUE || value == Double.MAX_VALUE) throw new OutOfRangeException();

        char[] sym = name.toCharArray();

        if (sym.length == 0) throw new InvalidSymbolException(name);

        for (int i = 0; i < sym.length; i++) {
            if (!(isLetter(sym[i]) || isDigit(sym[i]))) throw new InvalidSymbolException(name);
            if (i == 0 && isDigit(sym[i])) throw new InvalidSymbolException(name);
        }

        SymbolEntry entry = new SymbolEntry(value, true, timestamp);
        variables.put(name, entry);
    }

    public void addSensor(String name, double value) throws OutOfRangeException,
            InvalidSymbolException {

        addSensor(name, value, System.currentTimeMillis());
    }

    public void removeSensor(String name) throws UnknownSymbolException {
        if (!variables.containsKey(name)) throw new UnknownSymbolException(name);
        variables.remove(name);
    }

    public boolean sensorExists(String name) {
        return variables.containsKey(name);
    }

    public Double getVariable(String sym) throws UnknownSymbolException {
        if (!variables.containsKey(sym)) throw new UnknownSymbolException(sym);
        return variables.get(sym).value;
    }

    // Only get key-value pairs of variable name and value, without description or flags
    public HashMap<String, Double> getVariables() {
        HashMap<String, Double> result = new HashMap<>();
        for (Map.Entry<String, SymbolEntry> entry : variables.entrySet()) {
            result.put(entry.getKey(), entry.getValue().value);
        }
        return result;
    }

    // Only get key-value pairs of variable name and value, without description or flags
    public HashMap<String, SymbolEntry> getVariablesWithFlag() {
        return new HashMap<>(variables);
    }

    // Other getter methods that could be set to public if needed somewhere
    private ConcurrentHashMap<String, SymbolEntry> getEntries() {
        return variables;
    }

    private SymbolEntry getEntry(String sym) throws UnknownSymbolException {
        if (!variables.containsKey(sym)) throw new UnknownSymbolException(sym);
        return variables.get(sym);
    }

    // Not yet implemented
    private HashMap<String, Double> getSensors() {
        return new HashMap<>();
    }

    private void clearVariables() {
        variables.clear();
    }

    // Run the equations through the interpreter using the variables present in the HashMap
    public void calculate() throws IncorrectSyntaxException, UnknownSymbolException,
            IllegalWriteException, DivideByZeroException, OutOfRangeException, DomainException {
        currentTime = System.currentTimeMillis();

        // Iterate through all lines
        for (line = 0; line < equations.length; line++) {
            index = 0;
            char ch = read();

            // Skip any spaces or newlines
            while (ch == ' ') ch = next();
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
                if (ch != '\n') throw new IncorrectSyntaxException(line, index);
            } else throw new IncorrectSyntaxException(line, index);
        }
    }

    // Returns true if the line started with a keyword and calls the corresponding keyword method
    private boolean keyword(char ch) throws IncorrectSyntaxException, UnknownSymbolException,
            IllegalWriteException, DivideByZeroException, OutOfRangeException, DomainException {
        int start_index = index;

        // Build potential keyword from letters
        String sym = "";
        while (isLetter(ch)) {
            sym = sym + ch;
            ch = next();
        }
        while (ch == ' ') ch = next();

        // Jump to beginning of line and return false if not a keyword
        switch (sym) {
            case "if" -> {
                ch = if_statement(ch);
                return true;
            }
            case "goto" -> {
                ch = goto_label(ch);
                return true;
            }
            default -> {
                index = start_index;
                return false;
            }
        }
    }

    private char if_statement(char ch) throws IncorrectSyntaxException, UnknownSymbolException,
            IllegalWriteException, DivideByZeroException, OutOfRangeException, DomainException {
        boolean is_true;
        if (ch == '(') {
            ch = next();
            while (ch == ' ') ch = next();
            is_true = condition(ch);
            ch = read();
            if (ch != ')') throw new IncorrectSyntaxException(line, index);
        } else {
            is_true = condition(ch);
            ch = read();
        }

        // If statements are always followed by a goto statement
        String sym = "";
        while (isLetter(ch)) {
            sym = sym + ch;
            ch = next();
        }
        while (ch == ' ') ch = next();

        if (!sym.equals("goto")) throw new IncorrectSyntaxException(line, index);

        // Only execute goto statement if comparison returned true
        if (is_true) return goto_label(ch);
        return ch;
    }

    // Only here for clarity or future expansion
    private boolean condition(char ch) throws IncorrectSyntaxException,
            UnknownSymbolException, DivideByZeroException, OutOfRangeException, DomainException, IllegalWriteException {
        return disjunction(ch);
    }

    // Simple expression can be either
    // - term
    // - simple expression followed by adding operator (+/-) and another simple expression
    private boolean disjunction(char ch) throws IncorrectSyntaxException,
            UnknownSymbolException, DivideByZeroException, OutOfRangeException, DomainException, IllegalWriteException {
        boolean result = conjunction(ch);

        // Interpret +/- signs used for addition/subtraction
        ch = read();
        while (ch == '|') {
            ch = next();
            if (ch == '|') {
                ch = next();
                while (ch == ' ') ch = next();
                result = conjunction(ch) || result;
                ch = read();
            } else throw new IncorrectSyntaxException(line, index);
        }
        while (ch == ' ') ch = next();

        return result;
    }

    // Term can be either
    // - factor
    // - factor followed by multiplying operator (* or /) and another term
    private boolean conjunction(char ch) throws IncorrectSyntaxException,
            UnknownSymbolException, DivideByZeroException, OutOfRangeException, DomainException, IllegalWriteException {
        boolean result = bool(ch);

        ch = read();
        while (ch == '&') {
            ch = next();
            if (ch == '&') {
                ch = next();
                while (ch == ' ') ch = next();
                result = result && bool(ch);
                ch = read();
            } else throw new IncorrectSyntaxException(line, index);
        }
        while (ch == ' ') ch = next();

        return result;
    }

    private boolean bool(char ch) throws IncorrectSyntaxException,
            UnknownSymbolException, DivideByZeroException, OutOfRangeException, DomainException, IllegalWriteException {
        // Interpret for +/- sign before term
        boolean s = true;

        if (ch == '!') {
            s = false;
            ch = next();
            while (ch == ' ') ch = next();
        }

        boolean result;

        // If the first character is an opening parenthesis, it's an enclosed expression
        if (ch == '(') {
            ch = next();
            while (ch == ' ') ch = next();
            result = disjunction(ch);
            ch = read();
            if (ch == ')') ch = next();
            else throw new IncorrectSyntaxException(line, index);
        } else {
            result = comparison(ch);
        }

        ch = read();
        while (ch == ' ') ch = next();

        result = s && result;

        return result;
    }

    private boolean comparison(char ch) throws IncorrectSyntaxException, UnknownSymbolException,
            IllegalWriteException, DivideByZeroException, OutOfRangeException, DomainException {
        double value1 = expression(ch);
        ch = read();

        String op = "";
        while (isOperator(ch)) {
            op = op + ch;
            ch = next();
        }
        while (ch == ' ') ch = next();

        // Perform comparison depending on read relational operator
        double value2 = expression(ch);
        switch (op) {
            case "==":
                return (value1 == value2);
            case "!=":
                return (value1 != value2);
            case "<":
                return (value1 < value2);
            case "<=":
                return (value1 <= value2);
            case ">=":
                return (value1 >= value2);
            case ">":
                return (value1 > value2);
            default:
                throw new IncorrectSyntaxException(line, index);
        }
    }

    private char goto_label(char ch) throws IncorrectSyntaxException {
        String label1 = "";
        while (isLetter(ch) || isDigit(ch)) {
            label1 = label1 + ch;
            ch = next();
        }
        while (ch == ' ') ch = next();

        // Throw a syntax error if any unexpected characters come after the statement
        if (ch != '\n') throw new IncorrectSyntaxException(line, index);

        int currentLine = line;
        int currentIndex = index;

        // Iterate over each line, looking for the label at the beginning (preceded by a colon)
        while (line < equations.length - 1) {
            // Go to the next line and start at the first character
            line++;
            index = 0;
            ch = read();

            while (ch == ' ') ch = next();

            if (ch == ':') {
                ch = next();
                String label2 = "";
                while (isLetter(ch) || isDigit(ch)) {
                    label2 = label2 + ch;
                    ch = next();
                }
                while (ch == ' ') ch = next();

                // Throw a syntax error if any unexpected characters come after the statement
                if (ch != '\n') throw new IncorrectSyntaxException(line, index);

                // If the label is the label we are looking for, stop the search here
                // Otherwise continue on to the next line
                if (label1.equals(label2)) {
                    if (syntax_only) {
                        line = currentLine;
                        index = currentIndex;
                    }
                    return ch;
                }
            }
        }

        // Label could not be found
        throw new IncorrectSyntaxException(currentLine, currentIndex);
    }

    // Only here for clarity or future expansion
    private char equation(char ch) throws IncorrectSyntaxException, UnknownSymbolException,
            IllegalWriteException, DivideByZeroException, OutOfRangeException, DomainException {
        return assignment_statement(ch);
    }

    // Assign a value to a variable in the HashMap
    // Syntax: identifier := expression
    private char assignment_statement(char ch) throws IncorrectSyntaxException,
            UnknownSymbolException, IllegalWriteException, DivideByZeroException, OutOfRangeException, DomainException {
        SimpleEntry<String, Double> var = variable(ch);

        variableTime = 0;

        ch = read();
        if (ch != ':') throw new IncorrectSyntaxException(line, index);
        ch = next();
        if (ch != '=') throw new IncorrectSyntaxException(line, index);
        ch = next();
        while (ch == ' ') ch = next();

        double result = expression(ch);

        if (variableTime == 0)
            variableTime = currentTime;

        setSymbol(var.getKey(), new SymbolEntry(result, false, variableTime));

        ch = read();
        return ch;
    }

    // Build variable name, add to HashMap and retrieve value (null if previously undefined)
    private SimpleEntry<String, Double> variable(char ch) throws IncorrectSyntaxException {
        if (!isLetter(ch)) throw new IncorrectSyntaxException(line, index);

        // Build variable name from read characters
        String sym = "";
        while (isLetter(ch) || isDigit(ch)) {
            sym = sym + ch;
            ch = next();
        }
        while (ch == ' ') ch = next();

        SymbolEntry entry = getSymbol(sym);
        Double value = entry.value;

        if (entry.lastModified > variableTime)
            variableTime = entry.lastModified;

        return new SimpleEntry<String, Double>(sym, value);
    }

    // Only here for clarity or future expansion
    private double expression(char ch) throws IncorrectSyntaxException,
            UnknownSymbolException, DivideByZeroException, OutOfRangeException, DomainException {
        return simple_expression(ch);
    }

    // Simple expression can be either
    // - term
    // - simple expression followed by adding operator (+/-) and another simple expression
    private double simple_expression(char ch) throws IncorrectSyntaxException,
            UnknownSymbolException, DivideByZeroException, OutOfRangeException, DomainException {
        double result = term(ch);

        // Interpret +/- signs used for addition/subtraction
        ch = read();
        while (ch == '+' || ch == '-') {
            result = adding_operator(ch, result);
            ch = read();
        }
        while (ch == ' ') ch = next();

        return result;
    }

    // Apply adding operator to first simple expression and second simple expression
    private double adding_operator(char ch, double result) throws IncorrectSyntaxException,
            UnknownSymbolException, DivideByZeroException, OutOfRangeException, DomainException {
        char ch2 = ch;
        ch = next();
        while (ch == ' ') ch = next();
        if (ch2 == '+') result = result + term(ch);
        else result = result - term(ch);

        if (result == Double.MIN_VALUE || result == Double.MAX_VALUE) throw new OutOfRangeException(line, index);

        return result;
    }

    // Term can be either
    // - factor
    // - factor followed by multiplying operator (* or /) and another term
    private double term(char ch) throws IncorrectSyntaxException,
            UnknownSymbolException, DivideByZeroException, OutOfRangeException, DomainException {
        double result = factor(ch);

        ch = read();
        while (ch == '*' || ch == '/') {
            result = multiplying_operator(ch, result);
            ch = read();
        }
        while (ch == ' ') ch = next();

        return result;
    }

    // Apply multiplying operator (* or /) to first factor and new term
    private double multiplying_operator(char ch, double result) throws IncorrectSyntaxException,
            UnknownSymbolException, DivideByZeroException, OutOfRangeException, DomainException {
        char ch2 = ch;
        ch = next();
        if (ch2 == '*') {
            if (ch == '*') {
                ch = next();
                while (ch == ' ') ch = next();
                double fact = factor(ch);
                if (result < 0 && fact % 1 != 0) throw new DomainException("Root of negative number");
                result = Math.pow(result, fact);
            } else {
                while (ch == ' ') ch = next();
                result = result * factor(ch);
            }
        }
        else {
            while (ch == ' ') ch = next();
            double divisor = factor(ch);
            if (divisor == 0) throw new DivideByZeroException(line, index);
            result = result / divisor;
        }

        if (result == Double.MIN_VALUE || result == Double.MAX_VALUE) throw new OutOfRangeException(line, index);

        return result;
    }

    private double factor(char ch) throws IncorrectSyntaxException,
            UnknownSymbolException, DivideByZeroException, OutOfRangeException, DomainException {
        // Interpret for +/- sign before term
        double s = 1;
        if (ch == '+' || ch == '-') {
            s = sign(ch);
            ch = read();
        }

        double result;

        // If the first character is a letter, it's either a method or a variable
        if (isLetter(ch)) {
            int currentIndex = index;

            // Check if symbol is method or variable
            String sym = "";
            while (isLetter(ch) || isDigit(ch)) {
                sym = sym + ch;
                ch = next();
            }
            // If the symbol is followed by opening parenthesis, it's a method
            // Otherwise it's a variable
            if (ch == '(') {
                index = currentIndex;
                ch = read();

                result = method(ch);
            } else {
                index = currentIndex;
                ch = read();

                SimpleEntry<String, Double> var = variable(ch);
                if (var.getValue() == null) throw new UnknownSymbolException(var.getKey());
                result = var.getValue();
            }
        }

        // If the first character is a digit, it's a number
        else if (isDigit(ch)) result = unsigned_constant(ch);

            // If the first character is an opening parenthesis, it's an enclosed expression
        else if (ch == '(') {
            ch = next();
            while (ch == ' ') ch = next();
            result = expression(ch);
            ch = read();
            if (ch == ')') ch = next();
            else throw new IncorrectSyntaxException(line, index);
        }

        // If the first character is a line, it's an enclosed positive expression
        else if (ch == '|') {
            ch = next();
            while (ch == ' ') ch = next();
            result = Math.abs(expression(ch));
            ch = read();
            if (ch == '|') ch = next();
            else throw new IncorrectSyntaxException(line, index);
        }
        else throw new IncorrectSyntaxException(line, index);

        ch = read();
        while (ch == ' ') ch = next();

        result = s * result;

        if (result == Double.MIN_VALUE || result == Double.MAX_VALUE) throw new OutOfRangeException(line, index);

        return result;
    }

    private double sign(char ch) {
        double result;
        if (ch == '+') result = 1;
        else result = -1;

        ch = next();
        while (ch == ' ') ch = next();

        return result;
    }

    private double method(char ch) throws IncorrectSyntaxException,
            DivideByZeroException, DomainException, UnknownSymbolException, OutOfRangeException {
        // Read symbol name
        String sym = "";
        while (isLetter(ch) || isDigit(ch)) {
            sym = sym + ch;
            ch = next();
        }

        ch = next(); // read opening parenthesis
        while (ch == ' ') ch = next();

        // Queue of read parameters
        LinkedList<Double> param = new LinkedList<>();

        if (ch != ')') {
            param.push(expression(ch));

            // Read parameters and add them to the queue
            ch = read();
            while (ch == ',') {
                ch = next();
                while (ch == ' ') ch = next();
                param.push(expression(ch));
            }
            ch = read();
            if (ch == ')') ch = next();
            else throw new IncorrectSyntaxException(line, index);
        } else {
            ch = next();
        }
        // Call the corresponding method
        if (param.size() == 0) {
            return switch (sym) {
                case "PI" -> Math.PI;
                case "E" -> Math.E;
                default -> throw new UnknownSymbolException(sym);
            };
        }
        if (param.size() == 1) {
            switch (sym) {
                case "sin": return Math.sin(param.pop());
                case "cos": return Math.cos(param.pop());
                case "tan": return Math.tan(param.pop());
                case "asin": return Math.asin(param.pop());
                case "acos": return Math.acos(param.pop());
                case "atan": return Math.atan(param.pop());
                case "sqrt": {
                    if (param.peek() < 0) throw new DomainException("Square root of negative number");
                    return Math.sqrt(param.pop());
                }
                case "abs": return Math.abs(param.pop());
                case "floor": return Math.floor(param.pop());
                case "ceil": return Math.ceil(param.pop());
                case "ln": return Math.log(param.pop()); // log e
                case "log": return Math.log10(param.pop()); //log 10
                default: throw new UnknownSymbolException(sym);
            }
        }
        else if (param.size() == 2) {
            return switch (sym) {
                case "min" -> Math.min(param.pop(), param.pop());
                case "max" -> Math.max(param.pop(), param.pop());
                default -> throw new UnknownSymbolException(sym);
            };
        }
        else throw new IncorrectSyntaxException(line, index);
    }

    private double unsigned_constant(char ch) throws OutOfRangeException {
        String result = "";

        // Build number from digits
        while (isDigit(ch)) {
            result = result + ch;
            ch = next();
        }

        // Allow decimals, separated by a period or a comma
        if (ch == '.') { // if (ch == '.' || ch == ',') {
            result = result + '.';
            ch = next();
            while (isDigit(ch)) {
                result = result + ch;
                ch = next();
            }
        }

        while (ch == ' ') ch = next();

        // Though unlikely to be reached, it would probably be best to implement additional checks
        // following the conversion to ensure the value did not exceed the range permissible for a
        // double (i.e. the result is between Double.MIN_VALUE and Double.MAX_VALUE and is not INF)

        return Double.parseDouble(result);
    }
}
