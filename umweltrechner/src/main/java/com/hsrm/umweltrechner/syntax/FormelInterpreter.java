import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class FormelInterpreter {

    static class SymbolEntry {
        Double value;
        boolean read_only;
        long last_modified;

        public SymbolEntry(Double value, boolean read_only) {
            this.value = value;
            this.read_only = read_only;
            this.last_modified = System.currentTimeMillis();
        }

        public SymbolEntry(Double value) {
            this(value, false);
        }

        @Override
        public String toString() {
            return "{value="+value+", read_only="+read_only+"}";
        }
    }

    static class IncorrectSyntaxException extends Exception {
        int line;
        int ch;

        public IncorrectSyntaxException(int line, int index) {
            super("Incorrect syntax at line "+(line + 1)+" and character "+(index + 1));
            this.line = line + 1;
            this.ch = index + 1;
        }
    }

    static class UnknownVariableException extends Exception {
        String sym;

        public UnknownVariableException(String sym) {
            super("Unknown variable "+sym);
            this.sym = sym;
        }
    }

    static class IllegalWriteException extends Exception {
        String sym;

        public IllegalWriteException(String sym) {
            super("Can't change value of read-only variable "+sym);
            this.sym = sym;
        }
    }

    private char[][] equations;
    private int line = 0;
    private int index = 0;
    private HashMap<String, SymbolEntry> variables = new HashMap<>();

    // Since the program is constantly looking ahead one character, adding a newline
    // character ensures that there is at least one character left to read
    public FormelInterpreter(String equations) {
        equations = equations + '\n';
        this.equations = setup(removeComments(equations));
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

    // Get symbol value from variables HashMap
    private SymbolEntry getSymbol(String sym) {
        if (!variables.containsKey(sym)) {
            SymbolEntry entry = new SymbolEntry(null);
            variables.put(sym, entry);
            variables.get(sym).last_modified = System.currentTimeMillis();
            return entry;
        }
        return variables.get(sym);
    }

    // Add new symbol to variable HashMap, value initialized as null by default
    private SymbolEntry setSymbol(String sym, SymbolEntry entry) throws IllegalWriteException {
        if (variables.containsKey(sym) && variables.get(sym).read_only) throw new IllegalWriteException(sym);
        variables.put(sym, entry);
        variables.get(sym).last_modified = System.currentTimeMillis();
        return entry;
    }

    // Check that the Syntax is valid by doing one calculation run with a second interpreter instance
    private void checkSyntax(String newEquations) throws IncorrectSyntaxException, UnknownVariableException, IllegalWriteException {
        FormelInterpreter tester = new FormelInterpreter(newEquations);
        for (Map.Entry<String, SymbolEntry> entry : variables.entrySet()) {
            try {
                tester.setSymbol(entry.getKey(), entry.getValue());
            }
            catch (IllegalWriteException e) {
                // this should never happen
            }
        }
        tester.calculate();
    }

    // Set the equations to be used by the interpreter
    public void setEquations(String newEquations) throws IncorrectSyntaxException, UnknownVariableException, IllegalWriteException {
        checkSyntax(newEquations);
        newEquations = newEquations + '\n';
        equations = setup(removeComments(newEquations));
    }

    // Interface for backend, only to be used to add/change value of input variables
    public void addVariable(String sym, double value) {
        SymbolEntry entry = new SymbolEntry(value, true);
        variables.put(sym, entry);
        variables.get(sym).last_modified = System.currentTimeMillis();
    }

    public HashMap<String, SymbolEntry> getVariables() {
        return variables;
    }

    public SimpleEntry<String, SymbolEntry> getVariable(String sym) throws UnknownVariableException {
        if (!variables.containsKey(sym)) throw new UnknownVariableException(sym);
        return new SimpleEntry<>(sym, variables.get(sym));
    }

    // Only get key-value pairs of variable name and value, without description or flags
    public HashMap<String, Double> getVariableValues() {
        HashMap<String, Double> result = new HashMap<>();
        for (Map.Entry<String, SymbolEntry> entry : variables.entrySet()) {
            result.put(entry.getKey(), entry.getValue().value);
        }
        return result;
    }

    public void clearVariables() {
        variables.clear();
    }

    public String removeComments(String input) {
        char[] chars = (input + '\n').toCharArray();
        ArrayList<Character> buffer = new ArrayList<>();

        // Omit all characters between // (inclusive) and \n (exclusive)
        for (int i = 0; i < chars.length - 1; i++) {
            if (chars[i] == '/' && chars[i+1] == '/') {
                while(chars[i] != '\n') i++;
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

    // Run the equations through the interpreter using the variables present in the HashMap
    public void calculate() throws IncorrectSyntaxException, UnknownVariableException, IllegalWriteException {
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
                if(keyword(ch)) continue;
                ch = equation(ch);
                // Throw a syntax error if any unexpected characters come after the statement
                if (ch != '\n') throw new IncorrectSyntaxException(line, index);
            } else throw new IncorrectSyntaxException(line, index);
        }
    }

    private boolean keyword(char ch) throws IncorrectSyntaxException, UnknownVariableException, IllegalWriteException {
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
            case "if": ch = if_statement(ch); return true;
            case "goto": ch = goto_label(ch); return true;
            default: index = start_index; return false;
        }
    }

    private char if_statement(char ch) throws IncorrectSyntaxException, UnknownVariableException, IllegalWriteException {
        boolean is_true = comparison(ch);
        ch = read();

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

    private boolean comparison(char ch) throws IncorrectSyntaxException, UnknownVariableException, IllegalWriteException {
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
            case "==": return (value1 == value2);
            case "!=": return (value1 != value2);
            case "<": return (value1 < value2);
            case "<=": return (value1 <= value2);
            case ">=": return (value1 >= value2);
            case ">": return (value1 > value2);
            default: throw new IncorrectSyntaxException(line, index);
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
                if (label1.equals(label2)) return ch;
            }
        }

        // Label could not be found
        throw new IncorrectSyntaxException(line, index);
    }

    // Only here for clarity or future expansion
    private char equation(char ch) throws IncorrectSyntaxException, UnknownVariableException, IllegalWriteException {
        return assignment_statement(ch);
    }

    // Assign a value to a variable in the HashMap
    // Syntax: identifier := expression
    private char assignment_statement(char ch) throws IncorrectSyntaxException, UnknownVariableException, IllegalWriteException {
        SimpleEntry<String, Double> var = variable(ch);

        ch = read();
        if (ch != ':') throw new IncorrectSyntaxException(line, index);
        ch = next();
        if (ch != '=') throw new IncorrectSyntaxException(line, index);
        ch = next();
        while (ch == ' ') ch = next();

        double result = expression(ch);
        setSymbol(var.getKey(), new SymbolEntry(result, false));

        ch = read();
        return ch;
    }

    // Build variable name, add to HashMap and retrieve value (null if previously undefined)
    private SimpleEntry<String, Double> variable(char ch) throws IncorrectSyntaxException {
        if (!Character.isLetter(ch)) throw new IncorrectSyntaxException(line, index);

        // Build variable name from read characters
        String sym = "";
        while (isLetter(ch) || isDigit(ch)) {
            sym = sym + ch;
            ch = next();
        }
        while (ch == ' ') ch = next();

        Double value = getSymbol(sym).value;

        return new SimpleEntry<String, Double>(sym, value);
    }

    // Only here for clarity or future expansion
    private double expression(char ch) throws IncorrectSyntaxException, UnknownVariableException {
        return simple_expression(ch);
    }

    // Simple expression can be either
    // - term
    // - term preceded by a sign (+/-)
    // - simple expression followed by adding operator (+/-) and another simple expression
    private double simple_expression(char ch) throws IncorrectSyntaxException, UnknownVariableException {
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

    private double sign(char ch) {
        double result;
        if (ch == '+') result = 1;
        else result = -1;

        ch = next();
        while (ch == ' ') ch = next();

        return result;
    }

    // Apply adding operator to first simple expression and second simple expression
    private double adding_operator(char ch, double result) throws IncorrectSyntaxException, UnknownVariableException {
        char ch2 = ch;
        ch = next();
        while (ch == ' ') ch = next();
        if (ch2 == '+') result = result + term(ch);
        else result = result - term(ch);

        return result;
    }

    // Term can be either
    // - factor
    // - factor followed by multiplying operator (* or /) and another term
    private double term(char ch) throws IncorrectSyntaxException, UnknownVariableException {
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
    private double multiplying_operator(char ch, double result) throws IncorrectSyntaxException, UnknownVariableException {
        char ch2 = ch;
        ch = next();
        while (ch == ' ') ch = next();
        if (ch2 == '*') result = result * factor(ch);
        else result = result / factor(ch);

        return result;
    }

    private double factor(char ch) throws IncorrectSyntaxException, UnknownVariableException {
        // Interpret for +/- sign before term
        double s = 1;
        if (ch == '+' || ch == '-') {
            s = sign(ch);
            ch = read();
        }

        double result;

        // If the first character is a letter, it's a variable
        if (isLetter(ch)) {
            SimpleEntry<String, Double> var = variable(ch);
            if (var.getValue() == null) throw new UnknownVariableException(var.getKey());
            result = var.getValue();
        }

        // If the first character is a digit, it's a number
        else if (isDigit(ch)) result = unsigned_constant(ch);

            // If the first character is a opening parenthesis, it's an enclosed expression
        else if (ch == '(') {
            ch = next();
            while (ch == ' ') ch = next();
            result = expression(ch);
            ch = read();
            if (ch == ')') ch = next();
            else throw new IncorrectSyntaxException(line, index);
        } else throw new IncorrectSyntaxException(line, index);

        ch = read();
        while (ch == ' ') ch = next();

        return s * result;
    }

    private double unsigned_constant(char ch) {
        String result = "";

        // Build number from digits
        while (isDigit(ch)) {
            result = result + ch;
            ch = next();
        }

        // Allow decimals, separated by a period or a comma
        if (ch == '.' || ch == ',') {
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
