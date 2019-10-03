package de.tubs.variantwrynn.core.simpleimpl;

import de.tubs.variantwrynn.core.model.Artefact;

public class OrthogonalStringArtefact implements Artefact {
    public final String value;

    public OrthogonalStringArtefact(String value) {
        this.value = value;
    }

    @Override
    public float getSimilarityWith(Artefact artefact) {
        if (artefact instanceof OrthogonalStringArtefact) {
            if (((OrthogonalStringArtefact) artefact).value.equals(value)) {
                return 1;
            }
        }

        return 0;
    }

    @Override
    public String toString() {
        return value;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Artefact) {
            return super.equals(obj) && getSimilarityWith((Artefact) obj) == 1;
        }

        return false;
    }
}
