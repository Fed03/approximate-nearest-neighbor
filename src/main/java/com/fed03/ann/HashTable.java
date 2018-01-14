package com.fed03.ann;

import org.apache.commons.math3.linear.ArrayRealVector;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class HashTable {
    private final EuclideanHashFunction[] hashFunctions;
    private final int numberOfHashFunctions;
    private HashMap<Integer, List<ArrayRealVector>> table;

    public HashTable(int numberOfHashFunctions, HashFactory factory) {
        this.numberOfHashFunctions = numberOfHashFunctions;
        this.hashFunctions = new EuclideanHashFunction[numberOfHashFunctions];
        for (int i = 0; i < numberOfHashFunctions; i++) {
            hashFunctions[i] = factory.createHashFunc();
        }
    }

    public void add(ArrayRealVector vector) {
        int hashIndex = hash(vector);
        if (!table.containsKey(hashIndex)) {
            table.put(hashIndex, new ArrayList<>());
        }
        table.get(hashIndex).add(vector);
    }

    public List<ArrayRealVector> query(ArrayRealVector query) {
        int hashIndex = hash(query);
        if (table.containsKey(hashIndex)) {
            return table.get(hashIndex);
        }

        return new ArrayList<>();
    }

    private int hash(ArrayRealVector vector) {
        int[] hashes = new int[numberOfHashFunctions];
        for (int i = 0; i < hashFunctions.length; i++) {
            hashes[i] = hashFunctions[i].apply(vector);
        }
        return Arrays.hashCode(hashes);
    }
}
