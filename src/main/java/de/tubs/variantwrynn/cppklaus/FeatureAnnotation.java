package de.tubs.variantwrynn.cppklaus;

import de.ovgu.featureide.fm.core.configuration.Configuration;
import de.tubs.variantwrynn.core.model.Artefact;
import de.tubs.variantwrynn.util.fide.ConfigurationUtils;
import de.tubs.variantwrynn.util.fide.NodeUtils;
import org.prop4j.And;
import org.prop4j.Node;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

public class FeatureAnnotation<T> {
    public final List<AlignedArtefact<T>> codeFragments;
    private Node annotation = null;

    private final List<FeatureAnnotation<T>> children;
    private FeatureAnnotation<T> parent = null;

    public FeatureAnnotation() {
        children = new ArrayList<>();
        codeFragments = new ArrayList<>();
    }

    public void addArtefact(AlignedArtefact<T> codeFragment) {
        this.codeFragments.add(codeFragment);
        // We assume that this step is actually unnecessary
        this.codeFragments.sort(AlignedArtefact::compareTo);
    }

    public int getLocationBegin() {
        return codeFragments.isEmpty() ? Integer.MAX_VALUE-1 : codeFragments.get(0).getLocation();
    }

    public int getLocationEnd() {
        return codeFragments.isEmpty() ? Integer.MAX_VALUE : codeFragments.get(this.codeFragments.size() - 1).getLocation();
    }

    public void setAnnotation(Node node) {
        this.annotation = node;
    }

    public Node getAnnotation() {
        return annotation;
    }

    public boolean addChild(FeatureAnnotation<T> child) {
        if (child.parent == null) {
            children.add(child);
            child.parent = this;
            return true;
        } else {
            System.out.println("[MacroHierarchy.addChild] Child has already a parent! aborting");
            return false;
        }
    }

    public boolean removeChild(FeatureAnnotation<T> child) {
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

    public FeatureAnnotation<T> getParent() {
        return parent;
    }

    public void simplify() {
        Node me = getAnnotation();
        if (me != null)
            me.simplify();

        for (FeatureAnnotation<T> child : children)
            child.simplify();
    }

    public void cascadeFormulas() {
        Node me = getAnnotation();

        if (me != null) {
            for (FeatureAnnotation<T> child : children) {
                child.setAnnotation(
                        new And(me, child.getAnnotation())
                );
            }
        }

        for (FeatureAnnotation<T> child : children) {
            child.cascadeFormulas();
        }
    }

    public FeatureAnnotation<T> toVariant(final Configuration configuration) {
        // If this trace does not satisfy the given configuration, we can cut the entire subtree.
        if (this.getAnnotation() == null || ConfigurationUtils.isSatisfyingAssignment(configuration, this.getAnnotation())) {
            FeatureAnnotation<T> variant = this.cloneShallow();

            for (FeatureAnnotation<T> child : children) {
                FeatureAnnotation<T> childVariant = child.toVariant(configuration);
                if (childVariant != null) {
                    variant.addChild(childVariant);
                }
            }

            return variant;
        }

        return null;
    }

    public boolean isConjunctiveNormalForm() {
        Node me = getAnnotation();

        if (me == null || me.isConjunctiveNormalForm()) {
            for (FeatureAnnotation<T> child : children) {
                if (!child.isConjunctiveNormalForm())
                    return false;
            }

            return true;
        }

        return false;
    }

    public String getCode() {
        StringBuilder string = new StringBuilder();

        int artefactIndex = 0;
        for (FeatureAnnotation<T> child : children) {
            final int childLocation = child.getLocationBegin();

            while (artefactIndex < codeFragments.size() && codeFragments.get(artefactIndex).getLocation() < childLocation) {
                string.append(codeFragments.get(artefactIndex).getContent()).append("\n");
                ++artefactIndex;
            }

            string.append(child.getCode());
        }

        for (; artefactIndex < codeFragments.size(); ++artefactIndex) {
            string.append(codeFragments.get(artefactIndex).getContent()).append("\n");
        }

        return string.toString();
    }

    private String prettyPrint(String indent) {
        StringBuilder code = new StringBuilder();

        code.append(indent).append("{").append(NodeUtils.toString(this.annotation)).append("}").append("\n");

        String childIndent = indent + "  ";
        int artefactIndex = 0;
        for (FeatureAnnotation<T> child : children) {
            final int childLocation = child.getLocationBegin();

            while (artefactIndex < codeFragments.size() && codeFragments.get(artefactIndex).getLocation() < childLocation) {
                code.append(codeFragments.get(artefactIndex).prettyPrint(indent));
                ++artefactIndex;
            }

            code.append(child.prettyPrint(childIndent));
        }

        for (; artefactIndex < codeFragments.size(); ++artefactIndex) {
            code.append(codeFragments.get(artefactIndex).prettyPrint(indent));
        }

        return code.toString();
    }

    public String prettyPrint() {
        return prettyPrint("");
    }

    public FeatureAnnotation<T> cloneShallow() {
        FeatureAnnotation<T> copy = new FeatureAnnotation<>();
        Node myFormula = this.annotation;
        copy.annotation = myFormula == null ? null : myFormula.clone();
        // We do not have to clone the fragments as they are const and should stay unique.
        copy.codeFragments.addAll(this.codeFragments);
        return copy;
    }

    public FeatureAnnotation<T> cloneDeep() {
        FeatureAnnotation<T> copy = cloneShallow();

        for (FeatureAnnotation<T> child : this.children) {
            copy.addChild(child.cloneDeep());
        }

        return copy;
    }

    public void storeAt(T s) {

    }

    public boolean contains(Artefact artefact) {
        if (!(artefact instanceof AlignedArtefact<?>)) {
            return false;
        }

        for (AlignedArtefact<T> a : this.codeFragments) {
            if (a.equals(artefact)) {
                return true;
            }
        }

        for (FeatureAnnotation<T> child : this.children) {
            if (child.contains(artefact)) {
                return true;
            }
        }

        return false;
    }
}
