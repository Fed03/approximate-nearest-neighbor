package com.fed03.ann.hashes;

import corpus_texmex_reader.TexMexVector;

import java.util.Random;
import java.util.function.Function;

public class HashFunction implements Function<TexMexVector, Integer> {
    private final double w;
    private final int vectorDimension;
    private final double offset;
    private final TexMexVector randomProjection;

    HashFunction(double w, int vectorDimension) {
        this.w = w;
        this.vectorDimension = vectorDimension;
        Random rand = new Random();
        this.offset = calcOffset(rand);
        this.randomProjection = calcRandomProjection(rand);
    }

    public Integer apply(TexMexVector vector) {
        return (int) Math.floor((vector.dotProduct(randomProjection) + offset) / w);
    }

    private TexMexVector calcRandomProjection(Random rand) {
        TexMexVector projection = new TexMexVector(-1, vectorDimension);
        for (int i = 0; i < vectorDimension; i++) {
            projection.setEntry(i, rand.nextGaussian());
        }

        return projection;
    }

    private double calcOffset(Random rand) {
        return rand.nextDouble() * w;
    }
}
