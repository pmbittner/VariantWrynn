package de.tubs.variantwrynn.util.fide;

import de.ovgu.featureide.fm.core.base.IFeatureModel;
import de.ovgu.featureide.fm.core.base.IFeatureModelFactory;
import de.ovgu.featureide.fm.core.base.impl.ExtendedFeatureModelFactory;

public class FeatureModelCreation {
    public final IFeatureModel model;
    public final IFeatureModelFactory factory;

    public FeatureModelCreation(IFeatureModelFactory factory) {
        this.factory = factory;
        this.model = factory.createFeatureModel();
    }

    public FeatureModelCreation() {
        this(new ExtendedFeatureModelFactory());
    }

    public static IFeatureModel createFeatureModel() {
        return new FeatureModelCreation().model;
    }
}
