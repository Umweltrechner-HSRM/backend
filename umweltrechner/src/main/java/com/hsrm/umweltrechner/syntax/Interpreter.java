package com.hsrm.umweltrechner.syntax;

import com.hsrm.umweltrechner.syntax.Exception.*;

import java.util.HashMap;

public interface Interpreter {

    /**
     * Causes the equations in the interpreter to be overriden by the
     * new set of equations.
     *
     * As the given equations are not checked for validity before
     * overriding the existing equations, it is strongly recommended
     * to call {@link #checkSyntax(String)} before running this method.
     *
     * @param   newEquations   the equations to be used by the interpreter.
     */
    void setEquations(String newEquations);

    /**
     * Causes the given equations to be run through the interpreter
     * without affecting other equations currently being run. If the
     * method returns without throwing an exception, no error was
     * found in the given equations. Exceptions should therefore be
     * caught and forwarded to the user to amend any mistakes before
     * continuing.
     *
     * This method should always be run before calling
     * {@link #setEquations(String)} to avoid leaving the interpreter
     * in an unstable state.
     *
     * @param   equations   the equations to be checked.
     * @throws IncorrectSyntaxException if a syntax error has been encountered.
     * @throws UnknownSymbolException   if a given symbol does not exist
     *                                  as a valid key in the symbol table or
     *                                  if the value of a symbol was read but
     *                                  not initialized ({@code null} value).
     * @throws IllegalWriteException    if a read only symbol (sensor)
     *                                  would have been written.
     * @throws DivideByZeroException    if a value would have been divided
     *                                  by zero.
     * @throws OutOfRangeException      if a value has exceeded the
     *                                  permissible range for type double.
     *                                  ({@link Double#MIN_VALUE} -
     *                                  {@link Double#MAX_VALUE}).
     * @throws DomainException          if an operation would have made a
     *                                  value leave the domain of real
     *                                  numbers (no imaginary numbers).
     */
    void checkSyntax(String equations) throws IncorrectSyntaxException,
            UnknownSymbolException, IllegalWriteException, DivideByZeroException, OutOfRangeException, DomainException;

    /**
     * Causes an entry for the given sensor name to be created in the
     * symbol table and its value and timestamp to be set as provided.
     * Overrides the existing value and timestamp should a symbol
     * (sensor or otherwise) with the given name already exist in the
     * symbol table.
     *
     * It can therefore be helpful to use {@link #symbolExists(String)}
     * beforehand to avoid overriding any symbols defined by the user.
     *
     * Symbols added to the symbol table this way are always set to
     * read-only, meaning their value cannot be changed by the user.
     *
     * @param   name        the sensor name.
     * @param   value       the value to be assigned to the sensor.
     * @param   timestamp   the last time the sensor value was updated
     *                      in milliseconds since the epoch.
     * @throws OutOfRangeException if the value to be assigned to the
     *         sensor is not within the range of {@link Double#MIN_VALUE}
     *         and {@link Double#MAX_VALUE}.
     * @throws InvalidSymbolException if the given sensor name is not
     *         a valid symbol name as specified in the documentation.
     * @see System#currentTimeMillis()
     */
    void addSensor(String name, double value, long timestamp) throws OutOfRangeException, InvalidSymbolException;

    /**
     * Overloads {@link #addSensor(String, double, long)}.
     *
     * The timestamp defaults to the current time in milliseconds
     * since the epoch.
     * @see System#currentTimeMillis()
     */
    void addSensor(String name, double value) throws OutOfRangeException, InvalidSymbolException;

    /**
     * Causes the entry identified by the given symbol to be deleted
     * from the symbol table.
     *
     * @param   symbol   the symbol name.
     * @throws  UnknownSymbolException if the given symbol does not
     *          exist as a valid key in the symbol table.
     */
    void removeSymbol(String symbol) throws UnknownSymbolException;

    /**
     * Indicates whether a symbol (sensor or variable) exists as
     * a valid key in the symbol table of the interpreter.
     * Does not differentiate between initialized and uninitialized
     * entries in the symbol table (value may be {@code null}).
     *
     * @param   symbol   a symbol identifier.
     * @return  {@code true} if a symbol with the given name exists
     *          in the symbol table; {@code false} otherwise.
     */
    boolean symbolExists(String symbol);

    /**
     * Causes the interpreter to interpret the equations currently
     * held in memory by the interpreter and returns once all equations
     * have been done or an exception has occurred. The symbol table is
     * changed in the process.
     *
     * @throws IncorrectSyntaxException if a syntax error has been encountered.
     * @throws UnknownSymbolException   if a given symbol does not exist
     *                                  as a valid key in the symbol table or
     *                                  if the value of a symbol was read but
     *                                  not initialized ({@code null} value).
     * @throws IllegalWriteException    if a read only symbol (sensor)
     *                                  would have been written.
     * @throws DivideByZeroException    if a value would have been divided
     *                                  by zero.
     * @throws OutOfRangeException      if a value has exceeded the
     *                                  permissible range for type double.
     *                                  ({@link Double#MIN_VALUE} -
     *                                  {@link Double#MAX_VALUE}).
     * @throws DomainException          if an operation would have made a
     *                                  value leave the domain of real
     *                                  numbers (no imaginary numbers).
     */
    void calculate() throws IncorrectSyntaxException,
            UnknownSymbolException, IllegalWriteException, DivideByZeroException, OutOfRangeException, DomainException;

    /**
     * Returns key-value pairs of all symbols (including sensors) from the
     * symbol table. It is possible for the value of a symbol to be {@code null}.
     * Flags are omitted.
     *
     * @return   A {@code HashMap} object containing all symbols of the symbol
     *           table with their corresponding values.
     */
    HashMap<String, Double> getVariables();

    /**
     * Returns key-value pairs of all symbols from the symbol table.
     * It is possible for the real value of a symbol to be {@code null}.
     * However, non-{@code null} flags are also included.
     *
     * @return   A {@code HashMap} object containing all symbols of the
     *           symbol table with their corresponding values and flags.
     * @see #getVariables()
     * @see com.hsrm.umweltrechner.syntax.SymbolTable.SymbolEntry
     */
    HashMap<String, SymbolTable.SymbolEntry> getVariablesWithFlag();

    /**
     * Returns the value of a given symbol in the symbol table.
     * Flags are omitted.
     *
     * @param   symbol   a symbol identifier.
     * @return  the value of the given symbol.
     * @throws  UnknownSymbolException if the given symbol does not
     *          exist as a valid key in the symbol table.
     */
    Double getVariable(String symbol) throws UnknownSymbolException;

    /**
     * Causes the entire symbol table (including sensors) to be cleared.
     * This cannot be undone.
     *
     * @see #clearVariables()
     */
    void clearSymbolTable();

    /**
     * Causes the symbol table (excluding sensors) to be cleared.
     * This cannot be undone.
     *
     * @see #clearSymbolTable()
     */
    void clearVariables();
}
