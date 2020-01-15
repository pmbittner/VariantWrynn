package de.tubs.variantwrynn.ast.diff.edits;

import de.tubs.variantwrynn.ast.AST;
import de.tubs.variantwrynn.ast.diff.Insertion;

public class InsertTree implements Insertion {
    private long targetId;
    private int targetIndex; // i
    private AST treeToInsert; // U

    @Override
    public void applyTo(AST ast /* T */) {
        AST p = ast.getByID(targetId);
        if (p != null) {
            p.add(treeToInsert, targetIndex);
        }
    }
}
