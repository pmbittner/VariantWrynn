package de.tubs.variantwrynn.util;

import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Fixed size bit string.
 */
public class Bits {
    private static final int WordSize = Long.SIZE;
    private long[] words;
    private final int numBits;

    private int wordIndexOfGlobalIndex(int i) {
        return i / WordSize;
    }

    public Bits(int numBits) {
        this.numBits = numBits;
        words = new long[(int) Math.ceil((double) numBits / (double)WordSize)];
    }

    public Bits(int numBits, long decimalval) {
        this(numBits);
        words[0] = decimalval;
    }

    public Bits(Bits other) {
        this.words = Arrays.copyOf(other.words, other.words.length);
        this.numBits = other.numBits;
    }

    public static List<Bits> fromDecimals(int numBits, int... num) {
        List<Bits> bits = new ArrayList<>(numBits);

        for (int value : num) {
            bits.add(new Bits(numBits, value));
        }

        return bits;
    }

    public boolean getBit(int index) {
        final int w = wordIndexOfGlobalIndex(index);
        final int localIndex = index - w;
        return ((words[w] >>> localIndex) & 1) == 1;
    }

    public void setBit(int index) {
        final int w = wordIndexOfGlobalIndex(index);
        final int localIndex = index - w;
        words[w] |= 1 << localIndex;
    }

    public void clearBit(int index) {
        final int w = wordIndexOfGlobalIndex(index);
        final int localIndex = index - w;
        words[w] &= ~(1 << localIndex);
    }

    public void setBitTo(int index, boolean value) {
        if (value) {
            setBit(index);
        } else {
            clearBit(index);
        }
    }

    public Bits inlineAnd(Bits other) {
        assert(this.numBits == other.numBits);

        for (int i = 0; i < words.length; ++i) {
            words[i] &= other.words[i];
        }

        return this;
    }

    public Bits and(Bits other) {
        return new Bits(this).inlineAnd(other);
    }

    public Bits inlineOr(Bits other) {
        assert(this.numBits == other.numBits);

        for (int i = 0; i < words.length; ++i) {
            words[i] |= other.words[i];
        }

        return this;
    }

    public Bits or(Bits other) {
        return new Bits(this).inlineOr(other);
    }

    public Bits inlineXor(Bits other) {
        assert(this.numBits == other.numBits);

        for (int i = 0; i < words.length; ++i) {
            words[i] ^= other.words[i];
        }

        return this;
    }

    public Bits xor(Bits other) {
        return new Bits(this).inlineXor(other);
    }

    /**
     * Mostly, the words array is longer than the actual represented bit string.
     * This method sets all trailing bits not belonging to the represented bit string to 0.
     */
    private void cleanExtraBits() {
        final int localIndex = numBits - (words.length - 1) * WordSize;

        // TODO: Can we make mask creation more efficient?
        long mask = 0;
        for (int i = 0; i < localIndex; ++i) {
            mask <<= 1;
            mask += 1;
        }

        words[words.length - 1] &= mask;
    }

    public int size() {
        return numBits;
    }

    public int capacity() {
        return WordSize * words.length;
    }

    public int cardinality() {
        cleanExtraBits();

        int cardi = 0;

        for (long word : words) {
            cardi += Long.bitCount(word);
        }

        return cardi;
    }

    public BigInteger toBigInt() {
        return new BigInteger(toByteArray());
    }

    public byte[] toByteArray() {
        cleanExtraBits();

        ByteBuffer buf = ByteBuffer.allocate(words.length * Long.SIZE / Byte.SIZE);
        for (long word : words) {
            buf.putLong(word);
        }

        return buf.array();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();

        for (int w = 0; w < words.length; ++w) {
            for (int i = 0; i < WordSize && w*WordSize + i < numBits; ++i) {
                sb.append((words[w] & (1 << i)) > 0 ? "1" : "0");
            }
        }

        return sb.reverse().toString();
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Bits)) return false;

        Bits other = (Bits) obj;
        if (this.numBits == other.numBits) {
            this.cleanExtraBits();
            other.cleanExtraBits();
            return Arrays.equals(this.words, other.words);
        }

        return false;
    }
}
