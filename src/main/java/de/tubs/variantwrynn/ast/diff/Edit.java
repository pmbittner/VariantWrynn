package de.tubs.variantwrynn.ast.diff;

import de.tubs.variantwrynn.ast.AST;

public interface Edit {
    void applyTo(AST ast);
}
