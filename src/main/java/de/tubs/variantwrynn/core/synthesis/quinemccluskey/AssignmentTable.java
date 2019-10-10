package de.tubs.variantwrynn.core.synthesis.quinemccluskey;

import de.tubs.variantwrynn.util.Bits;

import java.util.List;

/**
 * Knuth: "Premature optimisation is the root of all evil."
 * Thus, we first just present a naive implementation.
 */
public interface AssignmentTable {
    void fillWith(List<Bits> satisfyingAssignments, List<Bits> unsatisfyingAssignments, List<Bits> dontcareAssignments);

    AssignmentTable mergeIntoNextTable();

    boolean isEmpty();

    void print();
}
