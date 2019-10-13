package de.tubs.variantwrynn.core.simpleimpl;

import de.ovgu.featureide.fm.core.base.IFeature;
import de.ovgu.featureide.fm.core.base.IFeatureModel;
import de.ovgu.featureide.fm.core.configuration.Configuration;
import de.ovgu.featureide.fm.core.configuration.Selection;
import de.tubs.variantwrynn.core.model.Artefact;
import de.tubs.variantwrynn.core.model.Variant;
import org.prop4j.Node;

import java.util.*;

public class ListVariant implements Variant {
    private List<Artefact> artefacts;
    private Configuration config;

    public ListVariant(IFeatureModel featureModel, Artefact... artefacts) {
        this.config = new Configuration(featureModel, true, true);
        this.artefacts = new ArrayList<>(Arrays.asList(artefacts));
    }

    public void add(Artefact a) {
        artefacts.add(a);
    }

    public void select(String featureName) {
        config.setManual(config.getSelectablefeature(featureName), Selection.SELECTED);
    }

    public boolean contains(Artefact artefact) {
        for (Artefact a : this.artefacts) {
            if (a.getSimilarityWith(artefact) == 1) {
                return true;
            }
        }

        return false;
    }

    @Override
    public Configuration getConfiguration() {
        return config;
    }

    public List<? extends Artefact> getArtefacts() {
        return artefacts;
    }
}
