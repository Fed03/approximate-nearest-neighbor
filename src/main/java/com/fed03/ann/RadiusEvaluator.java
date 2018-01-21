package com.fed03.ann;

import corpus_texmex_reader.TexMexVector;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.*;
import java.util.stream.Collectors;

public class RadiusEvaluator {
    private static final int N_RANDOM_QUERY = 50;
    private static final int EVAL_TIMEOUT_SECONDS = 30;
    private final List<TexMexVector> dataset;
    private final Random random;

    RadiusEvaluator(List<TexMexVector> dataset) {
        this.dataset = dataset;
        random = new Random();
    }

    public double getRadius() {
        double radius = 0;
        ExecutorService pool = Executors.newCachedThreadPool();
        List<Callable<Double>> func = new ArrayList<>(N_RANDOM_QUERY);
        for (int i = 0; i < N_RANDOM_QUERY; i++) {
            func.add(() -> getClosestDistance(dataset.get(random.nextInt(dataset.size()))));
        }

        try {
            final List<Future<Double>> futures = pool.invokeAll(func, EVAL_TIMEOUT_SECONDS, TimeUnit.SECONDS);
            List<Double> radiuses = new ArrayList<>(N_RANDOM_QUERY);
            for (Future<Double> future : futures) {
                radiuses.add(future.get());
            }
            double sum = radiuses.stream().reduce(0.0, (prev, next) -> prev + next);
            radius = sum / radiuses.size();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }

        return 0.90 * radius;
    }

    private double getClosestDistance(TexMexVector texMexVector) {
        TexMexVector closest = getClosestVector(texMexVector);
        return closest.getDistance(texMexVector);
    }

    private TexMexVector getClosestVector(TexMexVector texMexVector) {
        return (dataset.stream().sorted(new DistanceComparator(texMexVector)).collect(Collectors.toList())).get(0);
    }
}
