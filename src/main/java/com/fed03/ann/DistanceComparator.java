package com.fed03.ann;

import corpus_texmex_reader.TexMexVector;

import java.util.Comparator;

public class DistanceComparator implements Comparator<TexMexVector> {

    private final TexMexVector query;

    public DistanceComparator(TexMexVector query) {
        this.query = query;
    }

    @Override
    public int compare(TexMexVector one, TexMexVector two) {
        Double oneDistance = query.getDistance(one);
        Double twoDistance = query.getDistance(two);
        return oneDistance.compareTo(twoDistance);
    }
}
