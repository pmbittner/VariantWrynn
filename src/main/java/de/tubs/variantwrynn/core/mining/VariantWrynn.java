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
import org.prop4j.And;
import org.prop4j.Literal;
import org.prop4j.Node;
import org.prop4j.Or;

import java.util.ArrayList;
import java.util.Collections;
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
        List<Bits> v_dc;

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
        v_dc = ConfigurationUtils.getValidConfigurationsOf(fm, featureOrder);
        v_dc.removeAll(v_top);
        v_dc.removeAll(v_bot);

        Yield<List<Literal>> clauses = new QuineMcCluskey().synthesise(fm.getFeatureOrderList(), v_top, v_bot, v_dc);
        return new Yield<>(
                clauses::hasNext,
                () -> {
                    // If we have a single variant not containing a, a cannot be mapped to a mandatory feature!
                    // Current hack: We can remove mandatory features if there are non-mandatory features in the proposed mapping.
                    /*
                    List<Literal> clause = clauses.next();
                    /*/
                    List<Literal> clause = new ArrayList<>(clauses.next());

                    for (int i = 0; i < clause.size(); ++i) {
                        Literal l = clause.get(i);
                        if (fm.getFeature((String)l.var).getStructure().isMandatory()) {
                            clause.remove(i);
                            --i;
                        }
                    }
                    //*/

                    return new And(clause);
                }
        );
    }
}
