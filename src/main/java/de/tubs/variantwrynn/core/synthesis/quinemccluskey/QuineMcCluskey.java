package de.tubs.variantwrynn.core.synthesis.quinemccluskey;

import de.tubs.variantwrynn.core.synthesis.PropositionalFormulaSynthesiser;
import de.tubs.variantwrynn.util.Bits;
import de.tubs.variantwrynn.util.Yield;
import org.prop4j.Literal;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class QuineMcCluskey implements PropositionalFormulaSynthesiser {
    private static boolean DebugPrint = false;
    private Supplier<AssignmentTable> assignmentTableFactory;

    public QuineMcCluskey() {
        assignmentTableFactory = NaiveAssignmentTable::new;
    }

    @Override
    public Yield<List<Literal>> synthesise(
            List<String> variableNames,
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
            AssignmentTable nextTable = tables.get(iteration).mergeIntoNextTable();
            if (!nextTable.isEmpty()) {
                tables.add(nextTable);
            }
        }

        if (DebugPrint) {
            for (int iteration = 0; iteration < tables.size(); ++iteration) {
                System.out.println("\n==== [TABLE " + iteration + "] =================");
                System.out.println(tables.get(iteration));
                System.out.println("================================");
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

        List<Implicant> primeImplicants = new LinkedList<>();
        for (AssignmentTable table : tables) {
            primeImplicants.addAll(table.getPrimeImplicants(variableNames));
        }

        // We have to cover all lines represented in satisfyingAssignments.
        List<Implicant> minImplicants = new LinkedList<>();
        List<BigInteger> unsatLines = new LinkedList<>();

        Consumer<Implicant> chooseImplicant = i -> {
            minImplicants.add(i);
            primeImplicants.remove(i);
            unsatLines.removeAll(i.lines);
        };

        // 1.) If a line is covered by a single implicant only, that implicant is mandatory!
        {
            List<BigInteger> satLines = new LinkedList<>();
            for (Bits sat : satisfyingAssignments) {
                BigInteger lineNumber = sat.toBigInt();

                // If there is already a min implicant that satisfies lineNumber, we dont have to so anything.
                if (satLines.contains(lineNumber)) {
                    continue;
                }

                Implicant myUniqueImplicant = null;
                for (Implicant p : primeImplicants) {
                    if (p.lines.contains(lineNumber)) {
                        if (myUniqueImplicant == null) {
                            myUniqueImplicant = p;
                        } else {
                            unsatLines.add(lineNumber);
                            myUniqueImplicant = null;
                            break;
                        }
                    }
                }

                // If there is exactly one implicant
                if (myUniqueImplicant != null) {
                    chooseImplicant.accept(myUniqueImplicant);
                    satLines.addAll(myUniqueImplicant.lines);
                }
            }
        }

        // 2.) If there are still unsatisfied lines, lets choose implicants that cover most unsatisfied lines.
        //     Here is the point where multiple solutions are possible.
        while (!unsatLines.isEmpty()) {
            BigInteger line = unsatLines.get(0);
            Implicant implicantForLine = null;

            for (Implicant i : primeImplicants) {
                if (i.lines.contains(line) && (implicantForLine == null || implicantForLine.lines.size() < i.lines.size()))
                    implicantForLine = i;
            }

            assert(implicantForLine != null);

            chooseImplicant.accept(implicantForLine);
        }

        AtomicInteger i = new AtomicInteger();
        return new Yield<>(
                () -> i.get() < minImplicants.size(),
                () -> minImplicants.get(i.getAndIncrement()).clause
        );
    }
}
