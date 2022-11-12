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
            // Iterate through all characters of line
            while (index < equations[line].length - 1) {
                if (isLetter(ch)) {
                    ch = equation(ch);
                    // Throw a syntax error if any unexpected characters come after the statement
                    if (ch != '\n' && index != equations[line].length - 1)
                        throw new IncorrectSyntaxException(line, index);
                }
                // Skip any spaces or newlines in front of, between or after statements
                else if (ch == ' ' || ch == '\n') {
                    ch = next();
                } else throw new IncorrectSyntaxException(line, index);
            }
        }
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
