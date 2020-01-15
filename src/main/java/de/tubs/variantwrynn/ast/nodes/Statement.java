package de.tubs.variantwrynn.ast.nodes;

import de.tubs.variantwrynn.ast.AST;

public class Statement extends AST {
    public Statement() {
        super(true, true);
    }

    protected Statement(boolean fit, boolean propagates) {
        super(fit, propagates);
    }
}
