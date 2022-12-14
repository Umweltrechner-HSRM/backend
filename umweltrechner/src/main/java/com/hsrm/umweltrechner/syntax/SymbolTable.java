package com.hsrm.umweltrechner.syntax;

import com.hsrm.umweltrechner.syntax.Exception.*;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Class {@code SymbolTable} provides the functionality of
 * a symbol table containing key-value pairs. The value
 * associated with each key is a {@link SymbolEntry} object.
 * Intended to be managed by a corresponding Interpreter.
 */
public class SymbolTable {

    /**
     * Class {@code SymbolEntry} specifies the values of a
     * symbol entry in the {@link SymbolTable} class.
     */
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

    public final ConcurrentHashMap<String, SymbolEntry> symbols = new ConcurrentHashMap<>();

    // Get symbol value from variables HashMap
    public SymbolEntry getSymbol(String symbol) {
        if (!symbols.containsKey(symbol)) {
            SymbolEntry entry = new SymbolEntry(null);
            symbols.put(symbol, entry);
            return entry;
        }
        return symbols.get(symbol);
    }

    // Add new symbol to variable HashMap, value initialized as null by default
    public void setSymbol(String symbol, SymbolEntry entry) throws IllegalWriteException {
        if (symbols.containsKey(symbol) && symbols.get(symbol).readOnly)
            throw new IllegalWriteException(symbol);
        symbols.put(symbol, entry);
    }

    // Only to be used by surrounding application to add new read-only variables (sensors)
    public void addSensor(String name, SymbolEntry entry) {
        symbols.put(name, entry);
    }

    public void removeSymbol(String symbol) throws UnknownSymbolException {
        if (!symbols.containsKey(symbol)) throw new UnknownSymbolException(symbol);
        symbols.remove(symbol);
    }

    public boolean symbolExists(String symbol) {
        return symbols.containsKey(symbol);
    }

    public Double getVariable(String symbol) throws UnknownSymbolException {
        if (!symbols.containsKey(symbol)) throw new UnknownSymbolException(symbol);
        return symbols.get(symbol).value;
    }

    // Only get key-value pairs of variable name and value, without description or flags
    public HashMap<String, Double> getVariables() {
        HashMap<String, Double> result = new HashMap<>();
        for (Map.Entry<String, SymbolEntry> entry : symbols.entrySet()) {
            result.put(entry.getKey(), entry.getValue().value);
        }
        return result;
    }

    // Only get key-value pairs of variable name and value, without description or flags
    public HashMap<String, SymbolTable.SymbolEntry> getVariablesWithFlag() {
        return new HashMap<>(symbols);
    }

    // Clear entire symbol table, including sensors
    public void clearSymbolTable() {
        symbols.clear();
    }

    // Remove all variables, excluding sensors
    public void clearVariables() {
        for (Map.Entry<String, SymbolTable.SymbolEntry> entry : symbols.entrySet()) {
            if (!entry.getValue().readOnly) symbols.remove(entry.getKey());
        }
    }
}
