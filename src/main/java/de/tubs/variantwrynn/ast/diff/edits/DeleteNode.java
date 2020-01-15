package de.tubs.variantwrynn.ast.diff.edits;

import de.tubs.variantwrynn.ast.AST;
import de.tubs.variantwrynn.ast.diff.Deletion;

import java.util.List;

public class DeleteNode implements Deletion {
    private long targetID; // v

    @Override
    public void applyTo(AST ast) {
        AST removi = ast.getByID(targetID);
        int index = removi.getIndex();
        AST parent = removi.getParent();

        parent.remove(removi);
        List<AST> children = removi.removeChildren();
        parent.add(children, index);
    }
}
