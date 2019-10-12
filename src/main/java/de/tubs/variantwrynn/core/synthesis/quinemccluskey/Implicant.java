package de.tubs.variantwrynn.core.synthesis.quinemccluskey;

import org.prop4j.Literal;

import java.math.BigInteger;
import java.util.List;

public class Implicant {
    final List<Literal> clause;
    final List<BigInteger> lines;

    public Implicant(List<Literal> clause, List<BigInteger> lines) {
        this.clause = clause;
        this.lines = lines;
    }
}
