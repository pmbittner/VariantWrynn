package de.tubs.variantwrynn.core.synthesis;

import de.tubs.variantwrynn.util.Bits;
import de.tubs.variantwrynn.util.Yield;
import org.prop4j.Node;

import java.util.List;

public interface PropositionalFormulaSynthesiser {
    /**
     * Generates a set of propositional formulas for which the given assignments are satisfying.
     * TODO: As the result is always a DNF and we only want to return single clauses, a list of literals may be more
     *       efficient and easier to handle than a whole Node.
     * @return Yields the formulas one after the other as there are probably very many such formulas.
     */
    Yield<Node> synthesise(
            List<Bits> satisfyingAssignments,
            List<Bits> unsatisfyingAssignments,
            List<Bits> dontcareAssignments
    );
}
