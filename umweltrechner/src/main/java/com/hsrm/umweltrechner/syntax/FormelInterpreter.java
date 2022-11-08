import java.util.AbstractMap.SimpleEntry;
import java.util.HashMap;
import java.util.Map;

public class FormelInterpreter {

    static class IncorrectSyntaxException extends Exception {
        int index;

        public IncorrectSyntaxException(int index) {
            super("Incorrect syntax at character index " + index);
            this.index = index;
        }
    }

    static class UnknownVariableException extends Exception {
        String sym;

        public UnknownVariableException(String sym) {
            super("Unknown variable " + sym);
            this.sym = sym;
        }
    }

    private char[] equations;
    private int index = 0;
    private HashMap<String, Double> variables = new HashMap<>();

    // Since the program is constantly looking ahead one character, adding a newline
    // character ensures that there is at least one character left to read
    public FormelInterpreter(String equations) {
        equations = equations + "\n";
        this.equations = equations.toCharArray();
    }

    public FormelInterpreter() {
        this.equations = new char['\n'];
    }

    // Get next character
    private char next() {
        return equations[++index];
    }

    // Get current character
    private char read() {
        return equations[index];
    }

    private boolean isLetter(char ch) {
        // A-Z, a-z, underscore
        return Character.isLetter(ch) || ch == '_';
    }

    private boolean isDigit(char ch) {
        return Character.isDigit(ch);
    }

    // Get symbol value from variables HashMap
    private Double getSymbol(String sym) {
        if (!variables.containsKey(sym)) {
            variables.put(sym, null);
            return null;
        }
        return variables.get(sym);
    }

    // Add new symbol to variable HashMap, initialized as null by default
    private Double setSymbol(String sym, double value) {
        variables.put(sym, value);
        return value;
    }

    // Check that the Syntax is valid by doing one calculation run with a second interpreter instance
    private void checkSyntax(String newEquations) throws IncorrectSyntaxException, UnknownVariableException {
        FormelInterpreter tester = new FormelInterpreter(newEquations);
        for (Map.Entry<String, Double> entry : variables.entrySet()) {
            tester.addVariable(entry.getKey(), entry.getValue());
        }
        tester.calculate();
    }

    // Set the equations to be used by the interpreter
    public void setEquations(String newEquations) throws IncorrectSyntaxException, UnknownVariableException {
        checkSyntax(newEquations);
        newEquations = newEquations + "\n";
        equations = newEquations.toCharArray();
    }

    public void addVariable(String sym, double value) {
        variables.put(sym, value);
    }

    public HashMap<String, Double> getVariables() {
        return variables;
    }

    public void clearVariables() {
        variables.clear();
    }

    // Run the equations through the interpreter using the variables present in the HashMap
    public void calculate() throws IncorrectSyntaxException, UnknownVariableException {
        index = 0;
        char ch = read();
        // Loop through all characters of set equations
        while (index < equations.length - 1) {
            if (isLetter(ch)) {
                ch = equation(ch);
                // Throw a syntax error if any unexpected characters come after the statement
                if (ch != '\n' && index != equations.length - 1)
                    throw new IncorrectSyntaxException(index);
            }
            // Skip any spaces or newlines in front of, between or after statements
            else if (ch == ' ' || ch == '\n') {
                ch = next();
            }
            else throw new IncorrectSyntaxException(index);
        }
    }

    // Only here for clarity or future expansion
    private char equation(char ch) throws IncorrectSyntaxException, UnknownVariableException {
        return assignment_statement(ch);
    }

    // Assign a value to a variable in the HashMap
    // Syntax: identifier := expression
    private char assignment_statement(char ch) throws IncorrectSyntaxException, UnknownVariableException {
        SimpleEntry<String, Double> var = variable(ch);

        ch = read();
        if (ch != ':') throw new IncorrectSyntaxException(index);
        ch = next();
        if (ch != '=') throw new IncorrectSyntaxException(index);
        ch = next();
        while (ch == ' ') ch = next();

        double result = expression(ch);
        setSymbol(var.getKey(), result);

        ch = read();
        return ch;
    }

    // Build variable name, add to HashMap and retrieve value (null if previously undefined)
    private SimpleEntry<String, Double> variable(char ch) throws IncorrectSyntaxException {
        if (!Character.isLetter(ch)) throw new IncorrectSyntaxException(index);

        // Build variable name from read characters
        String sym = "";
        while (isLetter(ch) || isDigit(ch)) {
            sym = sym + ch;
            ch = next();
        }
        while (ch == ' ') ch = next();

        Double value = getSymbol(sym);

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
        // Interpret for +/- sign before term
        double s = 1;
        if (ch == '+' || ch == '-') {
            s = sign(ch);
            ch = read();
        }

        // Interpret +/- signs used for addition/subtraction
        double result = s * term(ch);
        ch = read();
        if (ch == '+' || ch == '-') {
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
        if (ch2 == '+' ) result = result + simple_expression(ch);
        else result = result - simple_expression(ch);

        return result;
    }

    // Term can be either
    // - factor
    // - factor followed by multiplying operator (* or /) and another term
    private double term(char ch) throws IncorrectSyntaxException, UnknownVariableException {
        double result = factor(ch);

        ch = read();
        if (ch == '*' || ch == '/') {
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
        if (ch2 == '*') result = result * term(ch);
        else result = result / term(ch);

        return result;
    }

    private double factor(char ch) throws IncorrectSyntaxException, UnknownVariableException {
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
            result = expression(ch);
            ch = read();
            if (ch == ')') ch = next();
            else throw new IncorrectSyntaxException(index);
        } else throw new IncorrectSyntaxException(index);

        ch = read();
        while (ch == ' ') ch = next();

        return result;
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
