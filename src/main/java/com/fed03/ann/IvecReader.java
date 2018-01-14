package com.fed03.ann;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class IvecReader extends ByteFileReader<Integer> {
    public IvecReader(String filename) {
        super(filename);
    }

    public List<int[]> getAllGroundTruthIndexes() {
        List<int[]> idxs = new ArrayList<>();
        try {
            while (true) {
                idxs.add(nextGroundTruthIndexes());
            }
        } catch (IOException ignored) {
        }

        return idxs;
    }

    public int[] nextGroundTruthIndexes() throws IOException {
        byte[] bytes = nextRawLine();

        int[] indexes = new int[getNumberOfFields()];
        for (int i = 0; i < indexes.length; i++) {
            indexes[i] = getFieldValueByIndex(bytes, i);
        }
        return indexes;
    }

    @Override
    protected Integer getFieldValueByIndex(byte[] components, int index) {
        return createByteBuffer(components, index).getInt();
    }
}
