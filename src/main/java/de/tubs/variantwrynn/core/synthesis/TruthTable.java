package de.tubs.variantwrynn.core.synthesis;

import java.util.BitSet;

public class TruthTable {
    private final int columns, rows;
    private BitSet table;

    public TruthTable(int numberOfVariables) {
        columns = numberOfVariables;
        rows = 1 << numberOfVariables; // ouch
        table = new BitSet(columns * rows);
    }
}
