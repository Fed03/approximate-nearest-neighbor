package com.fed03.ann.hashes;

public class HashFactory {
    private final double w;
    private final int vectorDimension;

    public HashFactory(double w, int vectorDimension) {
        this.w = w;
        this.vectorDimension = vectorDimension;
    }

    public HashFunction createHashFunc() {
        return new HashFunction(w, vectorDimension);
    }
}
