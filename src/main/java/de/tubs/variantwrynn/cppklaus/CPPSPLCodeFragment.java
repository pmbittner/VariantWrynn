package de.tubs.variantwrynn.cppklaus;

import de.tubs.variantwrynn.core.model.Artefact;

import java.io.PrintStream;

public class CPPSPLCodeFragment implements Artefact, Comparable<CPPSPLCodeFragment> {
    private final String text;

    /**
     * Index in a totally ordered list where the complete list represents the entire SPL.
     */
    private final int location;

    public CPPSPLCodeFragment(String text, int location) {
        this.text = text;
        this.location = location;
    }

    public String getText() {
        return text;
    }

    public int getLocation() {
        return location;
    }

    public void prettyPrint(PrintStream out, String indent) {
        out.println(indent + text.trim().replaceAll("[\\r\\n]+\\s*", "\n" + indent));
    }

    @Override
    public int compareTo(CPPSPLCodeFragment o) {
        return Integer.compare(this.location, o.location);
    }

    @Override
    public String toString() {
        return "Artefact{" +
                "location='" + location + '\'' +
                ", text=" + text +
                '}';
    }

    @Override
    public float getSimilarityWith(Artefact other) {
        if (other instanceof CPPSPLCodeFragment) {
            return this.location == ((CPPSPLCodeFragment) other).location ? 1 : 0;
        }

        return 0;
    }
}
