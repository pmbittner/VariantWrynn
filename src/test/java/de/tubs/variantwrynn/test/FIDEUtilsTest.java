package de.tubs.variantwrynn.test;

import de.ovgu.featureide.fm.core.FeatureModelAnalyzer;
import de.ovgu.featureide.fm.core.base.*;
import de.ovgu.featureide.fm.core.base.impl.Feature;
import de.ovgu.featureide.fm.core.editing.AdvancedNodeCreator;
import org.prop4j.*;
import org.prop4j.explain.solvers.SatSolver;
import org.prop4j.explain.solvers.SatSolverFactory;
import de.tubs.variantwrynn.util.fide.FeatureModelUtils;
import de.tubs.variantwrynn.util.fide.NodeUtils;
import org.junit.jupiter.api.Test;
import org.sat4j.specs.TimeoutException;

import static org.junit.jupiter.api.Assertions.*;

public class FIDEUtilsTest {
    @Test
    public void testFeatureLiterals() {
        IFeatureModel fm = FeatureModelUtils.createFeatureModel();
        IFeature a = new Feature(fm, "A");

        assertEquals(NodeUtils.reference(a), NodeUtils.reference(a));
        assertEquals(new Literal(a), new Literal(a));
    }

    @Test
    public void featuresInSatSolver() {
        IFeatureModel fm = FeatureModelUtils.createFeatureModel();
        IFeature a = new Feature(fm, "A");
        IFeature b = new Feature(fm, "B");

        Literal litA = new Literal(a);
        Literal litB = new Literal(b);

        final SatSolver solver = SatSolverFactory.getDefault().getSatSolver();
        solver.addFormulas(
                litA,
                litB,
                new Or(NodeUtils.negate(litA), new Not(litB))
        );

        assertFalse(solver.isSatisfiable());
    }

    @Test
    public void featureNamesInSatSolver() {
        IFeatureModel fm = FeatureModelUtils.createFeatureModel();
        IFeature a = new Feature(fm, "A");
        IFeature b = new Feature(fm, "B");

        Literal litA = new Literal(a.getName());
        Literal litB = new Literal(b.getName());

        final SatSolver solver = SatSolverFactory.getDefault().getSatSolver();
        solver.addFormulas(
                litA,
                litB,
                new Or(NodeUtils.negate(litA), new Not(litB))
        );

        assertFalse(solver.isSatisfiable());
    }

    @Test
    public void breakAlternativeWithAnalyzer() {
        final FeatureModelUtils.FeatureModelCreation fmCreator = new FeatureModelUtils.FeatureModelCreation();
        final IFeatureModelFactory factory = fmCreator.factory;
        final IFeatureModel model = fmCreator.model;

        IFeature root = new Feature(model, "Root");
        root.getStructure().setAlternative();
        model.getStructure().setRoot(root.getStructure());
        assertEquals(root, model.getStructure().getRoot().getFeature());

        IFeature a = new Feature(model, "A");
        IFeature b = new Feature(model, "B");
        root.getStructure().addChild(a.getStructure());
        root.getStructure().addChild(b.getStructure());

        // Why is this necessary?
        model.addFeature(root);
        model.addFeature(a);
        model.addFeature(b);

        //*
        Literal litA = new Literal(a);
        Literal litB = new Literal(b);
        /*/
        Literal litA = NodeUtils.reference(a);
        Literal litB = NodeUtils.reference(b);
        //*/

        Node node = new And(litA, litB);

        IConstraint constraint = factory.createConstraint(model, node);
        model.addConstraint(constraint);

        // inkonsisten BE vs AE
        FeatureModelAnalyzer analyzer = model.getAnalyser();

        boolean isSatisfiable = true;
        try {
            isSatisfiable = analyzer.isValid();
        } catch (TimeoutException t) {
            System.out.println("Timeout");
        }

        model.removeConstraint(constraint);

        assertFalse(isSatisfiable);
    }

    @Test
    public void breakAlternativeWithSolver() {
        final FeatureModelUtils.FeatureModelCreation fmCreator = new FeatureModelUtils.FeatureModelCreation();
        final IFeatureModel model = fmCreator.model;

        IFeature root = new Feature(model, "Root");
        root.getStructure().setAlternative();
        model.getStructure().setRoot(root.getStructure());
        assertEquals(root, model.getStructure().getRoot().getFeature());

        IFeature a = new Feature(model, "A");
        IFeature b = new Feature(model, "B");
        root.getStructure().addChild(a.getStructure());
        root.getStructure().addChild(b.getStructure());

        // Why is this necessary?
        model.addFeature(root);
        model.addFeature(a);
        model.addFeature(b);

        System.out.println(model.getFeatures());

        // Would be nice to have:
        //System.out.println(model.getStructure());

        /*
        Literal litA = new Literal(a);
        Literal litB = new Literal(b);
        /*/
        Literal litA = NodeUtils.reference(a);
        Literal litB = NodeUtils.reference(b);
        //*/

        Node node = new And(litA, litB);

        final SatSolver solver = SatSolverFactory.getDefault().getSatSolver();
        AdvancedNodeCreator nodeCreator = new AdvancedNodeCreator(model);
        nodeCreator.setModelType(AdvancedNodeCreator.ModelType.All);
        solver.addFormulas(node, nodeCreator.createNodes());
        boolean isSatisfiable = solver.isSatisfiable();

        assertFalse(isSatisfiable);
    }
}
