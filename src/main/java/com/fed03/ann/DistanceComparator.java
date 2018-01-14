package com.fed03.ann;

import org.apache.commons.math3.linear.ArrayRealVector;

import java.util.Comparator;

public class DistanceComparator implements Comparator<ArrayRealVector> {

    private final ArrayRealVector query;

    public DistanceComparator(ArrayRealVector query) {
        this.query = query;
    }

    @Override
    public int compare(ArrayRealVector one, ArrayRealVector two) {
        Double oneDistance = query.getDistance(one);
        Double twoDistance = query.getDistance(two);
        return oneDistance.compareTo(twoDistance);
    }
}
