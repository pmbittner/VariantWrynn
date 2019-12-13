package de.tubs.variantwrynn.cppklaus;

import de.ovgu.featureide.fm.core.configuration.Configuration;
import de.tubs.variantwrynn.util.fide.ConfigurationUtils;
import de.tubs.variantwrynn.util.fide.NodeUtils;
import org.prop4j.And;
import org.prop4j.Node;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

public class FeatureTrace {
    public final List<CPPSPLCodeFragment> codeFragments;
    private Node formula = null;

    private final List<FeatureTrace> children;
    private FeatureTrace parent = null;

    public FeatureTrace() {
        children = new ArrayList<>();
        codeFragments = new ArrayList<>();
    }

    public void addArtefact(CPPSPLCodeFragment codeFragment) {
        this.codeFragments.add(codeFragment);
        // We assume that this step is actually unnecessary
        this.codeFragments.sort(CPPSPLCodeFragment::compareTo);
    }

    public int getLocationBegin() {
        return codeFragments.isEmpty() ? Integer.MAX_VALUE-1 : codeFragments.get(0).getLocation();
    }

    public int getLocationEnd() {
        return codeFragments.isEmpty() ? Integer.MAX_VALUE : codeFragments.get(this.codeFragments.size() - 1).getLocation();
    }

    public void setFormula(Node node) {
        this.formula = node;
    }

    public Node getFormula() {
        return formula;
    }

    public boolean addChild(FeatureTrace child) {
        if (child.parent == null) {
            children.add(child);
            child.parent = this;
            return true;
        } else {
            System.out.println("[MacroHierarchy.addChild] Child has already a parent! aborting");
            return false;
        }
    }

    public boolean removeChild(FeatureTrace child) {
        if (child.parent == this) {
            if (children.remove(child)) {
                child.parent = null;
                return true;
            } else {
                System.err.println("[MacroHierarchy.removeChild] Could not remove child! This may be due to an inconsistent state.");
            }
        } else {
            System.err.println("[MacroHierarchy.removeChild] This is not my child! aborting");
        }

        return false;
    }

    public FeatureTrace getParent() {
        return parent;
    }

    public void simplify() {
        Node me = getFormula();
        if (me != null)
            me.simplify();

        for (FeatureTrace child : children)
            child.simplify();
    }

    public void cascadeFormulas() {
        Node me = getFormula();

        if (me != null) {
            for (FeatureTrace child : children) {
                child.setFormula(
                        new And(me, child.getFormula())
                );
            }
        }

        for (FeatureTrace child : children) {
            child.cascadeFormulas();
        }
    }

    public FeatureTrace toVariant(final Configuration configuration) {
        // If this trace does not satisfy the given configuration, we can cut the entire subtree.
        if (this.getFormula() == null || ConfigurationUtils.isSatisfyingAssignment(configuration, this.getFormula())) {
            FeatureTrace variant = this.cloneShallow();

            for (FeatureTrace child : children) {
                FeatureTrace childVariant = child.toVariant(configuration);
                if (childVariant != null) {
                    variant.addChild(childVariant);
                }
            }

            return variant;
        }

        return null;
    }

    public boolean isConjunctiveNormalForm() {
        Node me = getFormula();

        if (me == null || me.isConjunctiveNormalForm()) {
            for (FeatureTrace child : children) {
                if (!child.isConjunctiveNormalForm())
                    return false;
            }

            return true;
        }

        return false;
    }

    private void prettyPrint(PrintStream out, String indent) {
        out.println(indent + "{" + NodeUtils.toString(this.formula) + "}");

        String childIndent = indent + "  ";
        int artefactIndex = 0;
        for (FeatureTrace child : children) {
            final int childLocation = child.getLocationBegin();

            while (artefactIndex < codeFragments.size() && codeFragments.get(artefactIndex).getLocation() < childLocation) {
                codeFragments.get(artefactIndex).prettyPrint(out, indent);
                ++artefactIndex;
            }

            child.prettyPrint(out, childIndent);
        }

        for (; artefactIndex < codeFragments.size(); ++artefactIndex) {
            codeFragments.get(artefactIndex).prettyPrint(out, indent);
        }
    }

    public void prettyPrint(PrintStream out) {
        prettyPrint(out, "");
    }

    public FeatureTrace cloneShallow() {
        FeatureTrace copy = new FeatureTrace();
        Node myFormula = this.formula;
        copy.formula = myFormula == null ? null : myFormula.clone();
        // We do not have to clone the fragments as they are const and should stay unique.
        copy.codeFragments.addAll(this.codeFragments);
        return copy;
    }

    public FeatureTrace cloneDeep() {
        FeatureTrace copy = cloneShallow();

        for (FeatureTrace child : this.children) {
            copy.addChild(child.cloneDeep());
        }

        return copy;
    }
}
