package com.fed03.corpus_texmex_reader;

import org.apache.commons.math3.linear.ArrayRealVector;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class FvecReader extends ByteFileReader<Float> {
    public FvecReader(String filename) {
        super(filename);
    }

    public List<ArrayRealVector> getAllVectors() {
        List<ArrayRealVector> vectors = new ArrayList<>();
        try {
            while (true) {
                vectors.add(getNextVector());
            }
        } catch (IOException ignored) {
        }
        return vectors;
    }

    public List<ArrayRealVector> getNextVectors(int size) throws IOException {
        List<ArrayRealVector> vectors = new ArrayList<>(size);
        for (int i = 0; i < size; i++) {
            vectors.add(getNextVector());
        }

        return vectors;
    }

    public ArrayRealVector getNextVector() throws IOException {
        byte[] components = nextRawLine();

        ArrayRealVector vector = new ArrayRealVector(getVectorDimension());
        for (int i = 0; i < getVectorDimension(); i++) {
            float value = getFieldValueByIndex(components, i);
            vector.setEntry(i, value);
        }
        return vector;
    }

    @Override
    protected Float getFieldValueByIndex(byte[] components, int i) {
        return createByteBuffer(components, i).getFloat();
    }

    private int getVectorDimension() {
        return numberOfFields;
    }
}
