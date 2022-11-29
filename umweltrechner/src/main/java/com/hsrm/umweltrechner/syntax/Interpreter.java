package com.hsrm.umweltrechner.syntax;

import java.util.HashMap;

public interface Interpreter {

    // Set or change the equations used by the interpreter
    // Validity of syntax should be checked beforehand using the checkSyntax method
    void setEquations(String newEquations) throws IncorrectSyntaxException,
            UnknownSymbolException, IllegalWriteException;

    // Manually check the syntax of a given String object
    void checkSyntax(String equations) throws IncorrectSyntaxException,
            UnknownSymbolException, IllegalWriteException, DivideByZeroException, OutOfRangeException;

    // Set the sensors (read-only variables) to be used by the interpreter
    // Replaces existing values for sensors that are kept and adds/removes sensors to match new list
    // !!! Not currently implemented !!!
    // void setSensors(HashMap<String, Double> newSensors) throws OutOfRangeException,
    //      InvalidSymbolException;

    // Add a single sensor or update the value of an existing sensor
    // By default the current time in milliseconds is used as the timestamp
    void addSensor(String name, double value, long timestamp) throws OutOfRangeException, InvalidSymbolException;
    void addSensor(String name, double value) throws OutOfRangeException, InvalidSymbolException;

    // Remove a sensor. Throws UnknownVariableException if the given name can not be found
    void removeSensor(String sensor) throws UnknownSymbolException;

    // Check if a sensor exists
    boolean sensorExists(String sensor);

    // Perform calculations (interpret previously input equations)
    void calculate() throws IncorrectSyntaxException,
            UnknownSymbolException, IllegalWriteException, DivideByZeroException, OutOfRangeException;

    // Get all variables as name-value-pairs
    HashMap<String, Double> getVariables();

    HashMap<String, FormelInterpreter.SymbolEntry> getVariablesWithFlag();

    // Get the value of a single variable
    Double getVariable(String sym) throws UnknownSymbolException;
}