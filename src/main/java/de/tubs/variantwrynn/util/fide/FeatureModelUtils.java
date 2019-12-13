package de.tubs.variantwrynn.util.fide;

import de.ovgu.featureide.fm.core.base.IFeature;
import de.ovgu.featureide.fm.core.base.IFeatureModel;
import de.ovgu.featureide.fm.core.base.IFeatureModelFactory;
import de.ovgu.featureide.fm.core.base.impl.ExtendedFeatureModelFactory;

import java.util.ArrayList;
import java.util.List;

public class FeatureModelUtils {
    public static class FeatureModelCreation {
        public final IFeatureModel model;
        public final IFeatureModelFactory factory;

        public FeatureModelCreation(IFeatureModelFactory factory) {
            this.factory = factory;
            this.model = factory.createFeatureModel();
        }

        public FeatureModelCreation() {
            this(new ExtendedFeatureModelFactory());
        }

    }

    public static IFeatureModel createFeatureModel() {
        return new FeatureModelCreation().model;
    }

    public static List<String> getFeatureNames(IFeatureModel fm) {
        List<String> featureNames = new ArrayList<>(fm.getNumberOfFeatures());
        for (IFeature feature : fm.getFeatures()) {
            featureNames.add(feature.getName());
        }
        return featureNames;
    }
}
