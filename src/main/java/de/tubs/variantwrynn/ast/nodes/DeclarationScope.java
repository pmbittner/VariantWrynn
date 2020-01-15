package de.tubs.variantwrynn.ast.nodes;

import de.tubs.variantwrynn.ast.AST;

/**
 * FunctionalScope that declares some sort of structure, such as classes, functions, and namespaces.
 */
public class DeclarationScope extends AST {
    public DeclarationScope() {
        super(true, true);
    }

    protected DeclarationScope(boolean fit, boolean propagates) {
        super(fit, propagates);
    }
}
