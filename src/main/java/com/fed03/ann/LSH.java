package com.fed03.ann;

import com.fed03.ann.hashes.HashFactory;
import corpus_texmex_reader.TexMexVector;
import org.apache.commons.math3.distribution.NormalDistribution;
import org.apache.commons.math3.distribution.RealDistribution;

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import static java.lang.Math.*;

public final class LSH {
    private final List<TexMexVector> dataset;
    private final Index index;

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
    LSH(double delta, double eps, double binWidth, List<TexMexVector> dataset) {
        this.dataset = dataset;
        double p1 = calcClosenessProbability(binWidth, 1);
        double p2 = calcClosenessProbability(binWidth, 1 + eps);
        index = new Index(delta, dataset, p1, p2, new HashFactory(binWidth, dataset.get(0).getDimension()));
    }

    /**
     * @param k       The number of hash functions
     * @param L       The number of hash tables
     * @param dataset The dataset
     */
    LSH(int k, int L, List<TexMexVector> dataset) {
        this.dataset = dataset;
        index = new Index(dataset, k, L, new HashFactory(evalBinWidth(dataset), dataset.get(0).getDimension()));
    }

    public Index buildIndex() {
        final List<Future<Integer>> tablesFutures = index.build();
        final boolean result = tablesFutures.stream().mapToInt(future -> {
            try {
                return future.get();
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
                throw new RuntimeException();
            }
        }).allMatch(integer -> integer == dataset.size());

        if (!result) {
            throw new RuntimeException("Not all vectors are been added to the tables");
        }

        return index;
    }

    private double evalBinWidth(List<TexMexVector> dataset) {
        final RadiusEvaluator radiusEvaluator = new RadiusEvaluator(dataset);
        return 10 * radiusEvaluator.getRadius();
    }

    private static double calcClosenessProbability(double binWidth, double c) {
        RealDistribution normalDistrib = new NormalDistribution(0, 1);
        return 1 - (2 * normalDistrib.cumulativeProbability(-binWidth / c)) - ((2 / (sqrt(2 * PI) * binWidth / c)) * (1 - exp(-pow(binWidth, 2) / (2 * pow(c, 2)))));
    }
}
