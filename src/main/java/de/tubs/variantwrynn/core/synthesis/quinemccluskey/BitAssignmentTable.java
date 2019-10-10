package de.tubs.variantwrynn.core.synthesis.quinemccluskey;

import de.tubs.variantwrynn.util.Bits;

import java.util.Comparator;
import java.util.List;

/**
 * Unfinished
 */
public class BitAssignmentTable implements AssignmentTable {
    @Override
    public void fillWith(List<Bits> satisfyingAssignments, List<Bits> unsatisfyingAssignments, List<Bits> dontcareAssignments) {

    }

    @Override
    public AssignmentTable mergeIntoNextTable() {
        return null;
    }

    @Override
    public boolean isEmpty() {
        return false;
    }

    @Override
    public void print() {

    }

    /**
     * Represents the layout of following bit words:
     *  ____________________________________________________________
     * | Assignment | Dashes | LineNumber1 | LineNumber2 | Checked |
     * -------------------------------------------------------------
     * |   n bits   | n bits |     int     |     int     |  1 bit  |
     * -------------------------------------------------------------
     */
    private static class WordLayout {
        final int sizeof_Assignment;
        final int sizeof_Dashes;
        final int sizeof_LineNumber = Integer.SIZE;
        final int sizeof_Checked = 1;
        final int sizeof_Word;

        final int pos_Assignment;
        final int pos_Dashes;
        final int pos_LineNumber1;
        final int pos_LineNumber2;
        final int pos_Checked;

        WordLayout(int numVars) {
            sizeof_Assignment = numVars;
            sizeof_Dashes     = sizeof_Assignment;

            pos_Assignment  = 0;
            pos_Dashes      = sizeof_Assignment;
            pos_LineNumber1 = pos_Dashes      + sizeof_Dashes;
            pos_LineNumber2 = pos_LineNumber1 + sizeof_LineNumber;
            pos_Checked     = pos_LineNumber2 + sizeof_LineNumber;
            sizeof_Word     = pos_Checked     + sizeof_Checked;
        }
    }

    private final int numVars;
    private final WordLayout wordLayout;

    // Assignments in the Quine-McCluskey algorithm have 3 possible values, not just two.
    // 0 - false
    // 1 - true
    // 2 - dont care
    // As we want to support lots of configurations / assignments, the data representation should be as minimalistic
    // as possible. To represent three states, we need two bits. Hence, we can store eight values in one char because
    // a char has 16 bits in java.
    // TODO: This could be optimised perhaps by inlining everything into one huge BitSet. Can't we do this in C/C++ pleeeeeeeeeeeasssssssse. :(
    private Bits table;
    private List<Integer> tableIndices;

    BitAssignmentTable(int numVars, int numLines) {
        this.numVars = numVars;
        this.wordLayout = new WordLayout(numVars);
        this.table = new Bits(this.wordLayout.sizeof_Word * numLines);
    }

    private void newTableAt(int line) {
        // We assume that the list tableIndices is always sorted.
        // The caller is responsible for that.
        tableIndices.add(line);
    }

    private void initLine(int line, Bits assignment) {
        // Dashes = 0
        // LineNumber1 = toDecimal(assignment)
        // LineNumber2 = -1
        // checked = 0

        int address = this.wordLayout.sizeof_Word * line;
        //table.
        // fck java
    }

    /**
     * Creates the first group table of the Quine-McCluskey algorithm.
     * It groups the {@code satisfyingAssignments} by their number of ones, i.e., cardinality.
     *
     * @param numvars
     * @param satisfyingAssignments
     * @return
     */
    static BitAssignmentTable createFrom(final int numvars, List<Bits> satisfyingAssignments) {
        // sort by number of ones
        satisfyingAssignments.sort(Comparator.comparingInt(Bits::cardinality));

        BitAssignmentTable t = new BitAssignmentTable(numvars, satisfyingAssignments.size());

        int i = 0;
        int currentCardinality = satisfyingAssignments.get(0).cardinality();
        for (Bits assignment : satisfyingAssignments) {
            int assignmentsCardinality = assignment.cardinality();

            if (assignmentsCardinality > currentCardinality) {
                t.newTableAt(i);
                currentCardinality = assignmentsCardinality;
            }

            t.initLine(i, assignment);

            ++i;
        }

        return t;
    }
}
