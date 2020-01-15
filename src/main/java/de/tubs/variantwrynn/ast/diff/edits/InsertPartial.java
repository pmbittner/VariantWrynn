package de.tubs.variantwrynn.ast.diff.edits;

import de.tubs.variantwrynn.ast.AST;
import de.tubs.variantwrynn.ast.diff.Insertion;

import java.util.List;

public class InsertPartial implements Insertion {
    private long targetId;
    private int targetIndex; // i
    private AST treeToInsert; // U

    // i <= j
    private int j;
    private int newParent;
    private int k;

    @Override
    public void applyTo(AST ast /*T*/) {
        AST p = ast.getByID(targetId);
        List<AST> children = p.removeChildren(targetIndex, j);
        p.add(treeToInsert, targetIndex);
        AST u = treeToInsert.getByID(newParent);

        int childIndex = k;
        for (AST c : children) {
            u.add(c, childIndex);
            ++childIndex;
        }
    }
}
