package com.fed03.ann.hashes;

import corpus_texmex_reader.TexMexVector;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public class HashTable implements Serializable {
    private final HashFunction[] hashFunctions;
    private final int numberOfHashFunctions;
    private ConcurrentHashMap<Integer, List<TexMexVector>> table;
    private int size = 0;

    public HashTable(int numberOfHashFunctions, HashFactory factory) {
        this.numberOfHashFunctions = numberOfHashFunctions;
        table = new ConcurrentHashMap<>();
        this.hashFunctions = new HashFunction[numberOfHashFunctions];
        for (int i = 0; i < numberOfHashFunctions; i++) {
            hashFunctions[i] = factory.createHashFunc();
        }
    }

    public void add(TexMexVector vector) {
        privateAdd(vector);
    }

    public List<TexMexVector> query(TexMexVector query) {
        int hashIndex = hash(query);
        if (table.containsKey(hashIndex)) {
            return table.get(hashIndex);
        }

        return new ArrayList<>();
    }

    public int addDataset(Collection<TexMexVector> dataset) {
        dataset.forEach(this::privateAdd);
        return size();
    }

    private void privateAdd(TexMexVector vector) {
        int hashIndex = hash(vector);
        if (!table.containsKey(hashIndex)) {
            table.put(hashIndex, new ArrayList<>());
        }
        table.get(hashIndex).add(vector);
        size++;
    }

    private int size() {
        return size;
    }

    private int hash(TexMexVector vector) {
        int[] hashes = new int[numberOfHashFunctions];
        for (int i = 0; i < hashFunctions.length; i++) {
            hashes[i] = hashFunctions[i].apply(vector);
        }
        return Arrays.hashCode(hashes);
    }
}
