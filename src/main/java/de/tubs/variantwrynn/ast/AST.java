package de.tubs.variantwrynn.ast;

import de.tubs.variantwrynn.util.fide.NodeUtils;
import org.prop4j.Node;

import java.util.ArrayList;
import java.util.List;

import static de.tubs.variantwrynn.util.fide.NodeUtils.True;

public abstract class AST {
    private long id;
    private Value value;
    private Node featureMapping = True;

    private AST parent;
    private List<AST> children;

    /// Constraint: propagates => fit
    private boolean fit = true;
    private boolean propagates = true;

    public AST() {
        children = new ArrayList<>();
    }

    protected AST(boolean fit, boolean propagates) {
        if (!propagates || fit) {
            setFitness(fit);
            setPropagates(propagates);
        } else {
            throw new IllegalArgumentException("Violated constraint: propagates => fit!");
        }
    }

    public AST getByID(long id) {
        if (this.id == id) {
            return this;
        }

        // Todo: Make this more intelligent
        for (AST child : children) {
            if (child.getID() == id) {
                return child;
            }
        }

        for (AST child : children) {
            AST a = child.getByID(id);
            if (a != null) {
                return a;
            }
        }

        return null;
    }

    protected long getID() {
        return id;
    }

    /**
     * Syntactic validity of the AST has to be ensured by the user.
     * @param child
     */
    public void add(AST child, int index) {
        if (child.parent == null) {
            children.add(index, child);
            child.parent = this;
        } else {
            throw new IllegalArgumentException("Child \"" + child + "\" already has a parent!");
        }
    }

    public void add(List<AST> children, int index) {
        for (AST child : children) {
            if (child.parent != null) {
                throw new IllegalArgumentException("Child \"" + child + "\" already has a parent!");
            }
        }

        this.children.addAll(index, children);
    }

    public boolean remove(AST ast) {
        return this.children.remove(ast);
    }

    /**
     * Syntactic validity of the AST has to be ensured by the user.
     * @param child
     */
    public void add(AST child) {
        add(child, children.size());
    }

    public AST removeByID(long id) {
        AST ast = getByID(id);

        if (ast != null && ast.parent.remove(ast)) {
            return ast;
        }

        return null;
    }

    public List<AST> removeChildren(int l, int r) {
        if (r > l) {
            throw  new IllegalArgumentException("l (" + l + ") has to be smaller than r (" + r + ")!");
        }

        if (l < 0 || r >= children.size()) {
            throw new IndexOutOfBoundsException();
        }

        final int noRemovedChildren = r - l + 1;
        List<AST> removedChildren = new ArrayList<>(noRemovedChildren);

        for (int i = 0; i < noRemovedChildren; ++i) {
            removedChildren.add(children.remove(l));
        }

        return removedChildren;
    }

    public List<AST> removeChildren() {
        for (AST child : children) {
            child.parent = null;
        }

        List<AST> chiildrenToReturn = children;
        children = null;
        return chiildrenToReturn;
    }

    public boolean isRoot() {
        return parent == null;
    }

    public AST getParent() {
        return parent;
    }

    public int getIndex() {
        if (isRoot()) {
            return 0;
        }

        return parent.getChildren().indexOf(this);
    }

    public List<AST> getChildren() {
        return children;
    }

    public Value getValue() {
        return value;
    }

    public void setValue(Value value) {
        this.value = value;
    }

    public boolean isFeatureMappingFit() {
        return fit;
    }

    public boolean propagatesFeatureMapping() {
        return propagates;
    }

    /**
     * If a fit node is made unfit, its mapping gets deleted.
     * @param fit
     */
    public void setFitness(boolean fit) {
        this.fit = fit;

        if (!fit) {
            featureMapping = True;
            setPropagates(false);
        }
    }

    /**
     * If propagation is activated, the node will also be made feature mapping fit.
     * @param propagates
     */
    public void setPropagates(boolean propagates) {
        this.propagates = propagates;

        if (propagates) {
            setFitness(true);
        }
    }

    public void setFeatureMapping(Node node) {
        if (fit) {
            this.featureMapping = node;
        } else {
            throw new IllegalArgumentException("Only feature mapping fit nodes can be assigned a feature mapping!");
        }
    }

    public Node getFeatureMapping() {
        return this.featureMapping;
    }

    public Node getPresenceCondition() {
        if (isRoot()) {
            return getFeatureMapping();
        }

        return NodeUtils.AndSimplified(getParent().getPropagatedMapping(), getFeatureMapping());
    }

    protected Node getPropagatedMapping() {
        if (propagates) {
            return getPresenceCondition();
        }

        if (isRoot()) {
            return True;
        }

        return parent.getPropagatedMapping();
    }
}
