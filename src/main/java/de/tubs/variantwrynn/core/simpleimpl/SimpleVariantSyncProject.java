package de.tubs.variantwrynn.core.simpleimpl;

import de.ovgu.featureide.fm.core.base.IFeature;
import de.ovgu.featureide.fm.core.base.IFeatureModel;
import de.tubs.variantwrynn.core.model.Artefact;
import de.tubs.variantwrynn.core.model.Variant;
import de.tubs.variantwrynn.core.model.VariantSyncProject;

import java.util.List;

public class SimpleVariantSyncProject implements VariantSyncProject {
    private List<ListVariant> variants;
    private IFeatureModel fm;

    public SimpleVariantSyncProject(IFeatureModel fm, List<ListVariant> variants) {
        this.fm = fm;
        this.variants = variants;
    }

    @Override
    public List<ListVariant> getVariants() {
        return variants;
    }

    @Override
    public IFeatureModel getFeatureModel() {
        return fm;
    }

    public void print() {
        int variantIndex = 0;
        System.out.println("[SimpleVariantSyncProject.print]");

        for (ListVariant v : variants) {
            System.out.print("Variant " + variantIndex);// + " ------------------------------------------------------------------------------");

            List<IFeature> selectedFeatures = v.getConfiguration().getSelectedFeatures();
            //System.out.print("  Configuration = {" + selectedFeatures.get(0));
            System.out.print(" {" + selectedFeatures.get(0));
            for (int i = 1; i < selectedFeatures.size(); ++i) {
                System.out.print(", " + selectedFeatures.get(i).getName());
            }
            System.out.println("}");

            //*
            System.out.println("  Artefacts: ");
            for (Artefact a : v.getArtefacts()) {
                System.out.println("    " + a);
            }
            //*/

            ++variantIndex;
        }
    }
}
