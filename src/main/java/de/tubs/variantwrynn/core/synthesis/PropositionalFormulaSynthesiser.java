package de.tubs.variantwrynn.core.synthesis;

import de.tubs.variantwrynn.util.Bits;
import de.tubs.variantwrynn.util.Yield;
import org.prop4j.Literal;
import org.prop4j.Node;

import java.util.List;

public interface PropositionalFormulaSynthesiser {
    /**
     * Generates a set of propositional formulas for which the given assignments are satisfying.
     * TODO: As the result is always a DNF and we only want to return single clauses, a list of literals may be more
     *       efficient and easier to handle than a whole Node.
     * @param variableNames The interpretation of the assignments. variableName.get(i) is the name for the variable
     *                      identified by *Assignment.getBit(i).
     * @return Yields satisfying clauses one after the other as there are probably very many such formulas.
     *         Disjunct them to cover all cases.
     */
    Yield<List<Literal>> synthesise(
            List<String> variableNames,
            List<Bits> satisfyingAssignments,
            List<Bits> unsatisfyingAssignments,
            List<Bits> dontcareAssignments
    );
}
