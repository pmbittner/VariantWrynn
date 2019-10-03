package de.tubs.variantwrynn.core.model;

import de.ovgu.featureide.fm.core.configuration.Configuration;
import org.prop4j.Node;

public interface Variant {
    boolean contains(Artefact artefact);
    Configuration getConfiguration();
    boolean configurationSatisfies(Node formula);
}
