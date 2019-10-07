package de.tubs.variantwrynn.core.model;

import de.ovgu.featureide.fm.core.configuration.Configuration;
import de.tubs.variantwrynn.util.fide.ConfigurationUtils;
import org.prop4j.Node;

public interface Variant {
    boolean contains(Artefact artefact);
    Configuration getConfiguration();

    default boolean configurationSatisfies(Node formula) {
        return ConfigurationUtils.isSatisfyingAssignment(getConfiguration(), formula);
    }
}
