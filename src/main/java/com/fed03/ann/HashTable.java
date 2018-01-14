package com.fed03.ann;

public class HashTable {
    private final EuclideanHashFunction[] hashFunctions;

    public HashTable(int numberOfHashFunctions, HashFactory factory) {
        this.hashFunctions = new EuclideanHashFunction[numberOfHashFunctions];
        for (int i = 0; i < numberOfHashFunctions; i++) {
            hashFunctions[i] = factory.createHashFunc();
        }
    }
}
