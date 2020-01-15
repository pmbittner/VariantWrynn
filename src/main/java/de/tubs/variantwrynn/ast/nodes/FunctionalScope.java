package de.tubs.variantwrynn.ast.nodes;

/**
 * Scope with algorithmic content
 */
public class FunctionalScope extends Statement {
    /**
     * Scopes can be assigned mappings but do not propagate their mapping as their children are usually (!) independent.
     */
    public FunctionalScope() {
        super(true, false);
    }

    protected FunctionalScope(boolean fit, boolean propagates) {
        super(fit, propagates);
    }
}
