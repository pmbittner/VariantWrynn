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
    private int numBits;

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

        for (int i = 0; i < num.length; ++i) {
            bits.add(new Bits(numBits, num[i]));
        }

        return bits;
    }

    public boolean get(int i) {
        int wi = wordIndexOfGlobalIndex(i);
        int li = i - wi;
        return ((words[wi] >> li) & 1) == 1;
    }

    public void set(int index, boolean value) {

    }

    public Bits inlineAnd(Bits other) {
        assert(this.numBits == other.numBits);

        for (int i = 0; i < words.length; ++i) {
            words[i] &= other.words[i];
        }

        return this;
    }

    public Bits and(Bits other) {
        Bits res = new Bits(this);
        return res.inlineAnd(other);
    }

    public Bits inlineOr(Bits other) {
        assert(this.numBits == other.numBits);

        for (int i = 0; i < words.length; ++i) {
            words[i] |= other.words[i];
        }

        return this;
    }

    public Bits or(Bits other) {
        Bits res = new Bits(this);
        return res.inlineOr(other);
    }

    public Bits inlineXor(Bits other) {
        assert(this.numBits == other.numBits);

        for (int i = 0; i < words.length; ++i) {
            words[i] ^= other.words[i];
        }

        return this;
    }

    public Bits xor(Bits other) {
        Bits res = new Bits(this);
        return res.inlineXor(other);
    }

    public void cleanExtraBits() {
        final int localIndex = numBits - (words.length - 1) * WordSize;
        long mask = 0;
        for (int i = 0; i < localIndex; ++i) {
            mask += 1;
            mask <<= 1;
        }

        words[words.length - 1] &= mask;
    }

    public int size() {
        return numBits;
    }

    public long capacity() {
        return WordSize * words.length;
    }

    public int cardinality() {
        return 0;
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

        return sb.toString();
    }
}
