package de.tubs.variantwrynn.core.model;

import de.ovgu.featureide.fm.core.base.IFeatureModel;

import java.util.List;

public interface VariantSyncProject {
    List<? extends Variant> getVariants();
    IFeatureModel getFeatureModel();
}
