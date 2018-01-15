package com.fed03.ann;

import com.fed03.ann.hashes.HashFactory;
import com.fed03.ann.hashes.HashTable;
import org.apache.commons.math3.linear.ArrayRealVector;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static java.lang.Math.*;

class Index {
    private final int numberOfHashFunctions;
    private final int hashTablesNumber;
    private final HashTable[] hashTables;

    /**
     * @param delta       (1-delta) is the probability that at least one of the L projections
     *                    produces a collision between the query and the true nearest neighbor.
     *                    It is used to calc the number of hash table L.
     *                    0.10 is a good choice.
     * @param datasetSize The size of the dataset
     * @param p1          The probability that 2 vector close to each other
     *                    have the same hash must be greater then or equal to p1
     * @param p2          The probability that 2 vector not close to each other
     *                    have the same hash must be less then or equal to p2
     */
    Index(double delta, int datasetSize, double p1, double p2, HashFactory factory) {
        this.numberOfHashFunctions = calcNumberOfHashFunctions(datasetSize, p2);
        this.hashTablesNumber = calcHashTablesNumber(delta, p1, numberOfHashFunctions);
        this.hashTables = createHashTables(factory);
    }

    public int getNumberOfHashFunctions() {
        return numberOfHashFunctions;
    }

    public int getHashTablesNumber() {
        return hashTablesNumber;
    }

    public void add(ArrayRealVector vector) {
        for (HashTable table : hashTables) {
            table.add(vector);
        }
    }

    public List<ArrayRealVector> query(ArrayRealVector query, int numberOfNeighbors) {
        Set<ArrayRealVector> candidates = new HashSet<>();
        for (HashTable table : hashTables) {
            candidates.addAll(table.query(query));
        }
        return candidates.stream().sorted(new DistanceComparator(query)).limit(numberOfNeighbors).collect(Collectors.toList());
    }

    private HashTable[] createHashTables(HashFactory factory) {
        HashTable[] hashTables = new HashTable[hashTablesNumber];
        for (int i = 0; i < hashTablesNumber; i++) {
            hashTables[i] = new HashTable(numberOfHashFunctions, factory);
        }

        return hashTables;
    }

    private static int calcHashTablesNumber(double delta, double p1, int numberOfHashFunctions) {
        int roundedDelta = (int) round(log(delta));
        return (int) round(roundedDelta / log(1 - pow(p1, numberOfHashFunctions)));
    }

    private static int calcNumberOfHashFunctions(int datasetSize, double p2) {
        return (int) round(-log(datasetSize) / log(p2));
    }
}
