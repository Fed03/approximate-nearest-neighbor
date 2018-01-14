package com.fed03.ann;

import org.apache.commons.math3.linear.ArrayRealVector;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

public class FvecReader implements AutoCloseable {
    private static final int BUFFER_SIZE = 32768;
    private int vectorDimension;
    private InputStream inputStream;

    public FvecReader(String filename) {
        try {
            InputStream inputStream = new BufferedInputStream(new FileInputStream(getFile(filename)), BUFFER_SIZE);
            this.inputStream = inputStream;
            inputStream.mark(2);
            vectorDimension = inputStream.read();
            inputStream.reset();
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }
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
        byte[] components = new byte[getVectorDimension() * 4];
        inputStream.skip(4);
        if (inputStream.read(components) == -1) {
            throw new IOException("End of stream");
        }

        ArrayRealVector vector = new ArrayRealVector(getVectorDimension());
        for (int i = 0; i < getVectorDimension(); i++) {
            float value = ByteBuffer.wrap(components, i * 4, 4).getFloat();
            vector.setEntry(i, value);
        }
        return vector;
    }

    public int getVectorDimension() {
        return vectorDimension;
    }

    @Override
    public void close() throws IOException {
        inputStream.close();
    }

    private static String getFile(String filename) {
        return ClassLoader.getSystemClassLoader().getResource(filename).getFile();
    }
}
