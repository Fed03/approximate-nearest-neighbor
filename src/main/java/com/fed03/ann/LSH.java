package com.fed03.ann;

import com.fed03.ann.hashes.HashFactory;
import org.apache.commons.math3.distribution.NormalDistribution;
import org.apache.commons.math3.distribution.RealDistribution;
import org.apache.commons.math3.linear.ArrayRealVector;

import java.util.List;

public final class LSH {
    private final double delta;
    private final double eps;
    private final double binWidth;
    private final List<ArrayRealVector> dataset;
    private final int vectorDimension;

    /**
     * The probability that 2 vector close to each other
     * have the same hash must be greater then or equal to p1
     */
    private double p1;
    /**
     * The probability that 2 vector not close to each other
     * have the same hash must be less then or equal to p2
     */
    private double p2;

    /**
     * @param delta    (1-delta) is the probability that at least one of the L projections
     *                 produces a collision between the query and the true nearest neighbor.
     *                 It is used to calc the number of hash table L.
     *                 0.10 is a good choice.
     * @param eps      The approximation error, where vectors within R(1+eps) are likely to be returned.
     *                 Must be positive
     * @param binWidth It's the projection radius. It quantizes the projection in hash buckets,
     *                 therefore the number of points in every bucket depend on this param.
     * @param dataset  the training dataset
     */
    public LSH(double delta, double eps, double binWidth, List<ArrayRealVector> dataset) {
        this.delta = delta;
        this.eps = eps;
        this.binWidth = binWidth;
        this.dataset = dataset;
        this.vectorDimension = dataset.get(0).getDimension();
        this.p1 = calcClosenessProbability(1);
        this.p2 = calcClosenessProbability(getC());
    }

    public Index buildIndex() {
        Index index = new Index(delta, dataset.size(), p1, p2, new HashFactory(binWidth, vectorDimension));
        for (ArrayRealVector vector : dataset) {
            index.add(vector);
        }

        return index;
    }

    private double calcClosenessProbability(double c) {
        RealDistribution normalDistrib = new NormalDistribution(0, 1);
        return 1 - (2 * normalDistrib.cumulativeProbability(-binWidth / c)) - ((2 * c / Math.sqrt(2 * Math.PI) * binWidth) * (1 - Math.exp(-Math.pow(binWidth, 2) / 2 * Math.pow(c, 2))));
    }

    private double getC() {
        return 1 + eps;
    }
}
