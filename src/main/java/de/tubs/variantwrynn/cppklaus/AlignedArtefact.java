package de.tubs.variantwrynn.cppklaus;

import de.tubs.variantwrynn.core.model.Artefact;

import java.io.PrintStream;
import java.util.Objects;

/**
 * Artefacts are identified by their location.
 * For instance, in pre-processor based software product lines, the location of source code is unique.
 * @param <T> Type of the content of the artefact, e.g., String for source code.
 */
public class AlignedArtefact<T> implements Artefact, Comparable<AlignedArtefact<T>> {
    private final T content;

    /**
     * Index in a totally ordered list where the complete list represents the entire SPL.
     */
    private final int location;

    public AlignedArtefact(T content, int location) {
        this.content = content;
        this.location = location;
    }

    public T getContent() {
        return content;
    }

    public int getLocation() {
        return location;
    }

    public String prettyPrint(String indent) {
        String output;

        if (content instanceof String) {
            output = ((String) content).trim().replaceAll("[\\r\\n]+\\s*", "\n" + indent);
        } else {
            output = content.toString();
        }

        return indent + output;
    }

    @Override
    public int compareTo(AlignedArtefact o) {
        return Integer.compare(this.location, o.location);
    }

    @Override
    public String toString() {
        return "Artefact{" +
                "location='" + location + '\'' +
                ", content=" + content +
                '}';
    }

    @Override
    public float getSimilarityWith(Artefact other) {
        if (other instanceof AlignedArtefact) {
            return getSimilarityWith((AlignedArtefact<?>) other);
        }

        return 0;
    }

    public float getSimilarityWith(AlignedArtefact<?> other) {
        if (other.getContent().getClass().equals(this.content.getClass())) {
            return this.location == other.location ? 1 : 0;
        }

        return 0;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof AlignedArtefact)) return false;
        return getSimilarityWith((AlignedArtefact<?>) o) >= 1;
    }

    @Override
    public int hashCode() {
        return Objects.hash(content, location);
    }
}
