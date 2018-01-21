package com.fed03.ann;

import com.fed03.ann.hashes.HashFactory;
import com.fed03.ann.hashes.HashTable;
import corpus_texmex_reader.TexMexVector;

import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Collectors;

import static java.lang.Math.*;

class Index {
    private final int numberOfHashFunctions;
    private final int hashTablesNumber;
    private final HashTable[] hashTables;
    private final Collection<TexMexVector> dataset;

    /**
     * @param delta   (1-delta) is the probability that at least one of the L projections
     *                produces a collision between the query and the true nearest neighbor.
     *                It is used to calc the number of hash table L.
     *                0.10 is a good choice.
     * @param dataset The dataset
     * @param p1      The probability that 2 vector close to each other
     *                have the same hash must be greater then or equal to p1
     * @param p2      The probability that 2 vector not close to each other
     *                have the same hash must be less then or equal to p2
     */
    Index(double delta, Collection<TexMexVector> dataset, double p1, double p2, HashFactory factory) {
        this.dataset = dataset;
        this.numberOfHashFunctions = calcNumberOfHashFunctions(dataset.size(), p2);
        this.hashTablesNumber = calcHashTablesNumber(delta, p1, numberOfHashFunctions);
        this.hashTables = createHashTables(factory);
    }


    /**
     * @param dataset The dataset
     * @param k       The number of hash functions
     * @param L       The number of hash tables
     * @param factory Factory responsible of creating hash tables and their functions
     */
    Index(Collection<TexMexVector> dataset, int k, int L, HashFactory factory) {
        numberOfHashFunctions = k;
        hashTablesNumber = L;
        this.dataset = dataset;
        hashTables = createHashTables(factory);
    }

    public void add(TexMexVector vector) {
        for (HashTable table : hashTables) {
            table.add(vector);
        }
    }

    public Map<TexMexVector, List<TexMexVector>> query(List<TexMexVector> queries, int numberOfNeighbors) {
        ExecutorService pool = Executors.newCachedThreadPool();
        Map<TexMexVector, Future<List<TexMexVector>>> futures = new HashMap<>(queries.size());
        for (TexMexVector query : queries) {
            Future<List<TexMexVector>> future = pool.submit(() -> this.query(query, numberOfNeighbors));
            futures.put(query, future);
        }

        pool.shutdown();

        return futures.entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey, entrySet -> {
            try {
                return entrySet.getValue().get();
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
                return new ArrayList<>(0);
            }
        }));
    }

    public List<TexMexVector> query(TexMexVector query, int numberOfNeighbors) {
        Set<TexMexVector> candidates = new HashSet<>();
        for (HashTable table : hashTables) {
            candidates.addAll(table.query(query));
        }

        return candidates.stream()
                .sorted(new DistanceComparator(query))
                .limit(numberOfNeighbors)
                .collect(Collectors.toList());
    }

    public List<Future<Integer>> build() {
        ExecutorService pool = Executors.newFixedThreadPool(min(Runtime.getRuntime().availableProcessors() - 1, hashTables.length));
        List<Callable<Integer>> callables = new ArrayList<>(hashTablesNumber);
        for (HashTable table : hashTables) {
            callables.add(() -> table.addDataset(dataset));
        }
        try {
            return pool.invokeAll(callables);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
            pool.shutdown();
        }
    }

    private HashTable[] createHashTables(HashFactory factory) {
        HashTable[] hashTables = new HashTable[hashTablesNumber];
        for (int i = 0; i < hashTablesNumber; i++) {
            hashTables[i] = new HashTable(numberOfHashFunctions, factory);
        }

        return hashTables;
    }

    private static int calcHashTablesNumber(double delta, double p1, int numberOfHashFunctions) {
        return (int) ceil(log(delta) / log(1 - pow(p1, numberOfHashFunctions)));
    }

    private static int calcNumberOfHashFunctions(int datasetSize, double p2) {
        return (int) round(-log(datasetSize) / log(p2));
    }
}
