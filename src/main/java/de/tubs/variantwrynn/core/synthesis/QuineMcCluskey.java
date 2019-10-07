package de.tubs.variantwrynn.core.synthesis;

import de.tubs.variantwrynn.util.Yield;
import org.prop4j.Node;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.Comparator;
import java.util.List;

public class QuineMcCluskey implements PropositionalFormulaSynthesiser {
    public static class AssignmentTable {
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
        private BitSet table;
        private List<Integer> tableIndices;

        AssignmentTable(int numVars, int numLines) {
            this.numVars = numVars;
            this.wordLayout = new WordLayout(numVars);
            this.table = new BitSet(this.wordLayout.sizeof_Word * numLines);
        }

        private void newTableAt(int line) {
            // We assume that the list tableIndices is always sorted.
            // The caller is responsible for that.
            tableIndices.add(line);
        }

        private void initLine(int line, BitSet assignment) {
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
        static AssignmentTable createFrom(final int numvars, List<BitSet> satisfyingAssignments) {
            // sort by number of ones
            satisfyingAssignments.sort(Comparator.comparingInt(BitSet::cardinality));

            AssignmentTable t = new AssignmentTable(numvars, satisfyingAssignments.size());

            int i = 0;
            int currentCardinality = satisfyingAssignments.get(0).cardinality();
            for (BitSet assignment : satisfyingAssignments) {
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

    @Override
    public Yield<Node> synthesise() {
        /**
         * Pseudo code:
         *
         * Input: List<BitString> T, F, D sind Assignments für True, False und Dontcare.
         *
         * // possible optimisation: arrays
         * // Iteration (von links nach rechts) < Tabellenblock < Zeilen > > >:
         * List<List<List<BitString>>> table
         *
         * for BitString b in union(T, D):
         *   table[0][countOnes(b)].add(b)
         *
         * for (int iteration = 0; iteration < table.size(); ++iteration):
         *   for (int group = 0; group < table[iteration].size() - 1; ++group)
         *     for BitString a in table[iteration][group]:
         *       for BitString a in table[iteration][group + 1]:
         *         if a mergeable with b:
         *           check(a)
         *           check(b)
         *           table[iteration + 1][group].add(merge(a. b))
         *
         *
         * using Primimplikant = BitString;
         * List<Primimplikant> Primimplikanten = get all unchecked lines in table
         * // geeignete Teilmenge an Primimplikanten waehlen...
         * // dazu: Primimplikantentabelle aufbauen
         * Map<Primimplikant, List<int>> M;
         *
         * for Primimplikant p : Primimplikanten:
         *   for Line l in linesof(p):
         *     M[p].add(l)
         *
         * // Heuristik notwendig hier? Oder gibt es eine exakte Lösung?
         * // Es kann auf jeden Fall mehrere mögliche Lösungen geben.
         * List<BitString> implikanten;
         *
         * removeLine(M, L) = fun {
         *   for Entry e in M:
         *     e.value.remove(L)
         * }
         *
         * removePrimimplikant(M, P) = fun {
         *   M.removeKey(P);
         * }
         *
         * // Wenn es nur einen Primimplikanten gibt, der eine bestimmte Zeile abdeckt, muss dieser gewaehlt werden.
         * for Entry e in M:
         *   if e.value.size() == 1:
         *     implikanten.add(e.value.get(0));
         *     removeLine(M, e.key)
         *     removePrimimplikant(M, e.value);
         *
         * // restliche Implikanten waehlen
         * // hier: naive (?) Heuristik: Nimm immer den Primimplikanten, der die meisten Zeilen abdeckt,
         * while (!M.empty()):
         *   // find maximum
         *   Primimplikant maxp = M.size();
         *   int maxlines = 0;
         *   for Entry e in M:
         *     if e.val > maxlines:
         *       maxidx = e.index
         *       maxlines = e.key
         *
         *   // take it
         *   implikanten.add(maxp);
         *   removePrimimplikant(M, maxp);
         *
         * return toNodes(implikanten);
         */
        return null;
    }
}
