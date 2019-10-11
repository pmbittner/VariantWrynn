package de.tubs.variantwrynn.core.synthesis.quinemccluskey;

import de.tubs.variantwrynn.util.Bits;

import java.math.BigInteger;
import java.util.*;

public class NaiveAssignmentTable implements AssignmentTable {
    /**
     * Each row in the table needs the following properties:
     * - the assignment as a bitset
     * - the dash positions represented by a bitset
     * - a list of original line numbers, i.e., decimal numbers covered by the assignment
     * - checked status
     */
    private static class Row {
        final Bits assignment;
        final Bits dashes;
        final List<BigInteger> lines;
        // If a line is checked, we don't need the lines anymore.
        // Thus, lines == null could represent checked.
        private boolean checked;

        Row(Bits assignment) {
            this.assignment = assignment;
            this.dashes = new Bits(assignment.size());
            this.lines = new ArrayList<>(8);
            this.checked = false;
        }

        // Merge constructor
        Row(Row a, Row b) {
            this.assignment = b.assignment.and(a.assignment);

            // b.dashes would also be correct
            this.dashes = a.assignment.xor(b.assignment).inlineOr(a.dashes);

            this.lines = new ArrayList<>(a.lines.size() + b.lines.size());
            this.lines.addAll(a.lines);
            this.lines.addAll(b.lines);
        }

        public boolean isChecked() {
            return checked;
        }

        public void check() {
            checked = true;
        }

        public boolean isMergeableWith(Row other) {
            // Stupid java dev set clone return type to Object instead of BitSet...
            if (!dashes.equals(other.dashes))
                return false;

            // If assignment and other.assignment differ in one bit only.
            return assignment.xor(other.assignment).cardinality() == 1;
        }

        @Override
        public String toString() {
            StringBuilder r = new StringBuilder();

            for (int i = 0; i < assignment.size(); ++i) {
                if (dashes.getBit(i)) {
                    r.append("-");
                } else {
                    r.append(assignment.getBit(i) ? "1" : "0");
                }
            }

            r.append(checked ? " X " : "   ");
            r.append(lines.toString());

            return r.toString();
        }
    }

    private List<List<Row>> groups = null;

    public NaiveAssignmentTable() {

    }

    @Override
    public void fillWith(List<Bits> satisfyingAssignments, List<Bits> unsatisfyingAssignments, List<Bits> dontcareAssignments) {
        if (groups != null) {
            System.err.println("[NaiveAssignmentTable::fillWith] Warning: Overwriting existing table.");
        }

        // sort by number of ones
        satisfyingAssignments.addAll(dontcareAssignments);
        satisfyingAssignments.sort(Comparator.comparingInt(Bits::cardinality));

        /*
        System.out.println("Sorted assignments:");
        for (Bits b : satisfyingAssignments) {
            System.out.println("  " + b);
        }//*/

        final int numberOfGroups = satisfyingAssignments.get(satisfyingAssignments.size() - 1).cardinality();
        this.groups = new ArrayList<>(numberOfGroups);

        int lastMemberIndex = 0;
        for (int group = 1; group <= numberOfGroups; ++group) {
            int numberOfMembers;
            for (numberOfMembers = 0;
                 lastMemberIndex + numberOfMembers < satisfyingAssignments.size() && satisfyingAssignments.get(lastMemberIndex + numberOfMembers).cardinality() == group;
                 ++numberOfMembers);

            List<Row> members = new ArrayList<>(numberOfMembers);

            for (int i = 0; i < numberOfMembers; ++i) {
                Row r = new Row(satisfyingAssignments.get(lastMemberIndex + i));
                r.lines.add(r.assignment.toBigInt());
                members.add(r);
            }

            groups.add(members);
            lastMemberIndex += numberOfMembers;
        }
    }

    @Override
    public NaiveAssignmentTable mergeIntoNextTable() {
        final NaiveAssignmentTable next = new NaiveAssignmentTable();
        final int numberOfNextsGroups = this.groups.size() - 1;
        next.groups = new ArrayList<>(numberOfNextsGroups);

        for (int group = 0; group < numberOfNextsGroups; ++group) {
            /**
             * Worst case size is when all pairs are mergeable.
             * Then we would get sizeof(group1) * sizeof(group2) entries.
             * However, much less matches are expected.
             */
            List<Row> nextGroup = new ArrayList<>();
            next.groups.add(nextGroup);

            for (Row a : groups.get(group)) {
                for (Row b : groups.get(group + 1)) {
                    if (a.isMergeableWith(b)) {
                        a.check();
                        b.check();
                        nextGroup.add(new Row(a, b));
                    }
                }
            }
        }

        return next;
    }

    @Override
    public boolean isEmpty() {
        if (groups == null || groups.isEmpty()) return true;
        for (List<Row> group : groups) {
            if (!group.isEmpty()) {
                return false;
            }
        }
        return true;
    }

    @Override
    public void print() {
        final int numberOfVariables = groups.get(0).get(0).assignment.size();

        String separator = new String(new char[numberOfVariables + 3 /*lines*/ + 10 /*linenumbers*/]).replace("\0", "-");

        System.out.println(separator);
        for (List<Row> group : groups) {
            for (Row row : group) {
                System.out.println(row);
            }

            System.out.println(separator);
        }
    }
}
