package com.fed03.ann;

public class HashFactory {
    private final double w;
    private final int vectorDimension;

    public HashFactory(double w, int vectorDimension) {
        this.w = w;
        this.vectorDimension = vectorDimension;
    }

    public EuclideanHashFunction createHashFunc() {
        return new EuclideanHashFunction(w, vectorDimension);
    }
}
