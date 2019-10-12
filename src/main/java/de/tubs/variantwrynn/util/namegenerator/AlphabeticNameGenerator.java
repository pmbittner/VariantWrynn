package de.tubs.variantwrynn.util.namegenerator;

public class AlphabeticNameGenerator implements NameGenerator {
    private char[] alphabet = "abcdefghijklmnopqrstuvwxyz".toUpperCase().toCharArray();

    @Override
    public String getNameAtIndex(int i) {
        StringBuilder name = new StringBuilder();

        while (i >= alphabet.length) {
            name.insert(0, alphabet[i % alphabet.length]);
            i = (i / alphabet.length) - 1; // -1 because I don't know
        }

        return alphabet[i] + name.toString();
    }
}
