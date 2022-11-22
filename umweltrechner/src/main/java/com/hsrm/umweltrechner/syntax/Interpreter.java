package com.hsrm.umweltrechner.syntax;

import java.util.HashMap;

public interface Interpreter {

    // Set or change the equations used by the interpreter
    // Validity of syntax should be checked beforehand using the checkSyntax method
    void setEquations(String newEquations) throws
            FormelInterpreter.IncorrectSyntaxException,
            FormelInterpreter.UnknownVariableException,
            FormelInterpreter.IllegalWriteException;

    // Manually check the syntax of a given String object
    void checkSyntax(String equations) throws
            FormelInterpreter.IncorrectSyntaxException,
            FormelInterpreter.UnknownVariableException,
            FormelInterpreter.IllegalWriteException;

    // Set the sensors (read-only variables) to be used by the interpreter
    // Replaces existing values for sensors that are kept and adds/removes sensors to match new list
    // !!! Not currently implemented !!!
    void setSensors(HashMap<String, Double> newSensors);

    // Add a single sensor or update the value of an existing sensor
    // By default the current time in milliseconds is used as the timestamp
    void addSensor(String name, double value, long timestamp);
    void addSensor(String name, double value);

    // Remove a sensor. Throws UnknownVariableException if the given name can not be found
    void removeSensor(String sensor) throws FormelInterpreter.UnknownVariableException;

    // Check if a sensor exists
    boolean sensorExists(String sensor);

    // Perform calculations (interpret previously input equations)
    void calculate() throws
            FormelInterpreter.IncorrectSyntaxException,
            FormelInterpreter.UnknownVariableException,
            FormelInterpreter.IllegalWriteException;

    // Get all variables as name-value-pairs
    HashMap<String, Double> getVariables();

    HashMap<String, FormelInterpreter.SymbolEntry> getVariablesWithFlag();

    // Get the value of a single variable
    Double getVariable(String sym) throws FormelInterpreter.UnknownVariableException;
}
