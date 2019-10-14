package de.tubs.variantwrynn.core.simpleimpl;

import de.tubs.variantwrynn.core.model.Artefact;
import org.prop4j.Node;

public class GroundTruthArtefact<T> implements Artefact {
    private final Node groundTruthMapping;
    private final T value;

    public GroundTruthArtefact(T value, Node groundTruthMapping) {
        this.value = value;
        this.groundTruthMapping = groundTruthMapping;
    }

    @Override
    public float getSimilarityWith(Artefact artefact) {
        if (artefact instanceof GroundTruthArtefact) {
            if (((GroundTruthArtefact) artefact).value.equals(value)) {
                return 1;
            }
        }

        return 0;
    }

    public Node getGroundTruthMapping() {
        return groundTruthMapping;
    }

    @Override
    public String toString() {
        return value.toString();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Artefact) {
            return super.equals(obj) && getSimilarityWith((Artefact) obj) == 1;
        }

        return false;
    }
}
