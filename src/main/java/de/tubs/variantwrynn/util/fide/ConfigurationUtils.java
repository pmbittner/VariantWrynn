package de.tubs.variantwrynn.util.fide;

import de.ovgu.featureide.fm.core.base.IFeature;
import de.ovgu.featureide.fm.core.base.IFeatureModel;
import de.ovgu.featureide.fm.core.configuration.Configuration;
import de.tubs.variantwrynn.util.Bits;
import org.prop4j.Node;

import java.util.*;

public class ConfigurationUtils {
    public static boolean isSatisfyingAssignment(Configuration config, Node formula) {
        Map<Object, Boolean> assignment = new HashMap<>();

        for (IFeature f : config.getFeatureModel().getFeatures()) {
            assignment.put(f.getName(), false);
        }

        for (IFeature f : config.getSelectedFeatures()) {
            assignment.put(f.getName(), true);
        }

        return formula.getValue(assignment);
    }

    public static Bits toAssignment(Configuration config, List<String> featureOrder) {
        final Set<String> selectedFeatures = config.getSelectedFeatureNames();
        Bits assignment = new Bits(featureOrder.size());

        int i = 0;
        for (String f : featureOrder) {
            assignment.setBitTo(i, selectedFeatures.contains(f));
            ++i;
        }

        return assignment;
    }

    public static List<Bits> getValidConfigurationsOf(IFeatureModel fm, List<String> featureOrder) {
        // FIXME: Not implemented
        return new ArrayList<>(0);
    }
}
