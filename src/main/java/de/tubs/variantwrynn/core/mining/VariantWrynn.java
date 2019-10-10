package de.tubs.variantwrynn.core.mining;

import de.ovgu.featureide.fm.core.base.IFeature;
import de.ovgu.featureide.fm.core.base.IFeatureModel;
import de.tubs.variantwrynn.core.model.Artefact;
import de.tubs.variantwrynn.core.model.Variant;
import de.tubs.variantwrynn.core.model.VariantSyncProject;
import de.tubs.variantwrynn.core.synthesis.quinemccluskey.QuineMcCluskey;
import de.tubs.variantwrynn.util.Bits;
import de.tubs.variantwrynn.util.Yield;
import de.tubs.variantwrynn.util.fide.ConfigurationUtils;
import org.prop4j.Node;

import java.util.ArrayList;
import java.util.List;

public class VariantWrynn {
    private VariantSyncProject vsProject;

    public VariantWrynn(VariantSyncProject vsProject) {
        this.vsProject = vsProject;
    }

    public Yield<Node> recommendFeatureMappingFor(Artefact a) {
        final IFeatureModel fm = vsProject.getFeatureModel();
        final List<IFeature> featureOrder = new ArrayList<>(fm.getNumberOfFeatures());
        for (String fName : fm.getFeatureOrderList()) {
            featureOrder.add(fm.getFeature(fName));
        }

        List<Bits> v_top = new ArrayList<>();
        List<Bits> v_bot = new ArrayList<>();
        List<Bits> v_dc  = new ArrayList<>();

        for (Variant v : vsProject.getVariants()) {
            Bits c = ConfigurationUtils.toAssignment(v.getConfiguration(), featureOrder);

            if (v.contains(a)) {
                v_top.add(c);
            } else {
                v_bot.add(c);
            }
        }

        // List<Variant> v_dontcare = new ArrayList<>();
        // v_dontcare are all variants / configurations that have no implementation but are implicitly given by the featuremodel.
        // As these aren't concrete variants, instances of the Variant interface dont make sense here and only would litter our precious memory.
        // We need them for the derivation with the Quine-McCluskey algorithm in the next step.
        // Maybe, bitsets suffice.

        return new QuineMcCluskey().synthesise(v_top, v_bot, v_dc);
    }
}
