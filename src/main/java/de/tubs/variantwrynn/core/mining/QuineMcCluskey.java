package de.tubs.variantwrynn.core.mining;

import java.util.ArrayList;
import java.util.BitSet;

public class QuineMcCluskey {
    public static class AssignmentTable {
        private final int numVars;

        // Assignments in the Quine-McCluskey algorithm have 3 possible values, not just two.
        // 0 - false
        // 1 - true
        // 2 - dont care
        // As we want to support lots of configurations / assignments, the data representation should be as minimalistic
        // as possible. To represent three states, we need two bits. Hence, we can store eight values in one char because
        // a char has 16 bits in java.
        // TODO: This could be optimised perhaps by inlining everything into one huge BitSet. Can't we do this in C/C++ pleeeeeeeeeeeasssssssse. :(
        private ArrayList<BitSet> table;

        public AssignmentTable(int numVars) {
            this.numVars = numVars;

        }

        public void addAssignment() {

        }
    }
}
