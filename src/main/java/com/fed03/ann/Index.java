package com.fed03.ann;

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
