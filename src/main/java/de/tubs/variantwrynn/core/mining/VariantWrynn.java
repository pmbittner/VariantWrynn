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
import de.tubs.variantwrynn.util.fide.NodeUtils;
import org.prop4j.And;
import org.prop4j.Literal;
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
        List<String> featureOrder = new ArrayList<>(fm.getFeatureOrderList());
        List<Literal> featureContext = new ArrayList<>(featureOrder.size());

        for (String feature : featureOrder) {
            featureContext.add(NodeUtils.reference(feature));
        }

        return recommendFeatureMappingFor(a, featureContext);
    }

    public Yield<Node> recommendFeatureMappingFor(Artefact a, List<Literal> featureContext) {
        final IFeatureModel fm = vsProject.getFeatureModel();

        // First naive version for considering featureContext:
        // We enforce that only features in the featureContext can be in the final formula.
        // We omit negative literals for now.
        List<String> featureOrder = new ArrayList<>(featureContext.size());
        for (Literal literal : featureContext) {
            //if (literal.positive) {
                featureOrder.add(literal.var.toString());
            //}
        }

        List<Bits> v_top = new ArrayList<>();
        List<Bits> v_bot = new ArrayList<>();
        List<Bits> v_dc;

        // If v_bot is not empty, i.e., there is at least one variant not containing a,
        // a cannot be mapped to any core feature.
        // -> But cant it be an interaction in theory? CoreFeature and SomeOtherNonCoreFeature?
        if (vsProject.getVariants().stream().anyMatch(v -> !v.contains(a))) {
            for (IFeature f : fm.getFeatures()) {
                // FIXME: Has to be core instead of mandatory!
                /*
                if (f.getStructure().isMandatory()) {
                    featureOrder.remove(f.getName());
                }//*/
            }
        }

        for (Variant v : vsProject.getVariants()) {
            Bits c = ConfigurationUtils.toAssignment(v.getConfiguration(), featureOrder);

            if (v.contains(a)) {
                v_top.add(c);
            } else {
                v_bot.add(c);
            }
        }

        // v_dontcare are all variants / configurations that have no implementation but are implicitly given by the featuremodel.
        // As these aren't concrete variants, instances of the Variant interface dont make sense here and only would litter our precious memory.
        // We need them for the derivation with the Quine-McCluskey algorithm in the next step.
        // Maybe, bitsets suffice.
        v_dc = ConfigurationUtils.getValidConfigurationsOf(fm, featureOrder);
        v_dc.removeAll(v_top);
        v_dc.removeAll(v_bot);

        Yield<List<Literal>> clauses = new QuineMcCluskey().synthesise(featureOrder, v_top, v_bot, v_dc);
        return new Yield<>(
                clauses::hasNext,
                () -> new And(clauses.next())
        );
    }
}
