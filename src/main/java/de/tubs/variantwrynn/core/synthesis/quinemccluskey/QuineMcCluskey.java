package de.tubs.variantwrynn.core.synthesis.quinemccluskey;

import de.tubs.variantwrynn.core.synthesis.PropositionalFormulaSynthesiser;
import de.tubs.variantwrynn.util.Bits;
import de.tubs.variantwrynn.util.Yield;
import org.prop4j.Node;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public class QuineMcCluskey implements PropositionalFormulaSynthesiser {
    private Supplier<AssignmentTable> assignmentTableFactory;

    public QuineMcCluskey() {
        assignmentTableFactory = NaiveAssignmentTable::new;
    }

    @Override
    public Yield<Node> synthesise(
            List<Bits> satisfyingAssignments,
            List<Bits> unsatisfyingAssignments,
            List<Bits> dontcareAssignments
    ) {
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
         */
        // index in this list is the algorithm iteration
        List<AssignmentTable> tables = new ArrayList<>();

        {
            AssignmentTable firstGroups = assignmentTableFactory.get();
            firstGroups.fillWith(satisfyingAssignments, unsatisfyingAssignments, dontcareAssignments);
            tables.add(firstGroups);
        }

        for (int iteration = 0; iteration < tables.size(); ++iteration) {
            tables.get(iteration).print();
            AssignmentTable nextTable = tables.get(iteration).mergeIntoNextTable();
            if (!nextTable.isEmpty()) {
                tables.add(nextTable);
            }
        }

        /**
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
        return new Yield<Node>(
                () -> {return false;},
                () -> {return null;}
        );
    }
}
