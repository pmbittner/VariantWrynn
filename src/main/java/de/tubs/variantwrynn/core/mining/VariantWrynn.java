package de.tubs.variantwrynn.core.mining;

import de.tubs.variantwrynn.core.model.Artefact;
import de.tubs.variantwrynn.core.model.Variant;
import de.tubs.variantwrynn.core.model.VariantSyncProject;
import org.prop4j.Node;

import java.util.ArrayList;
import java.util.List;

public class VariantWrynn {
    private VariantSyncProject vsProject;

    public VariantWrynn(VariantSyncProject vsProject) {
        this.vsProject = vsProject;
    }

    public List<Node> recommendFeatureMappingFor(Artefact a) {
        List<Node> recommendations = new ArrayList<>();

        List<Variant> v_top = new ArrayList<>();
        List<Variant> v_bot = new ArrayList<>();

        for (Variant v : vsProject.getVariants()) {
            if (v.contains(a)) {
                v_top.add(v);
            } else {
                v_bot.add(v);
            }
        }

        // List<Variant> v_dontcare = new ArrayList<>();
        // v_dontcare are all variants / configurations that have no implementation but are implicitly given by the featuremodel.
        // As these aren't concrete variants, instances of the Variant interface dont make sense here and only would litter our precious memory.
        // We need them for the derivation with the Quine-McCluskey algorithm in the next step.
        // Maybe, bitsets suffice.




        return recommendations;
    }
}
