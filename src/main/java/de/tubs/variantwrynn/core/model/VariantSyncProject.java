package de.tubs.variantwrynn.core.model;

import de.ovgu.featureide.fm.core.base.IFeatureModel;
import de.tubs.variantwrynn.core.simpleimpl.ListVariant;

import java.util.List;

public interface VariantSyncProject {
    List<ListVariant> getVariants();
    IFeatureModel getFeatureModel();
}
