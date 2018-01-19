package corpus_texmex_reader;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class FvecReader extends ByteFileReader<Float> {
    private int index = 0;

    public FvecReader(String filename) {
        super(filename);
    }

    public List<TexMexVector> getAllVectors() {
        List<TexMexVector> vectors = new ArrayList<>();
        try {
            while (true) {
                vectors.add(getNextVector());
            }
        } catch (IOException ignored) {
        }
        return vectors;
    }

    public List<TexMexVector> getNextVectors(int size) throws IOException {
        List<TexMexVector> vectors = new ArrayList<>(size);
        for (int i = 0; i < size; i++) {
            vectors.add(getNextVector());
        }

        return vectors;
    }

    public TexMexVector getNextVector() throws IOException {
        byte[] components = nextRawLine();

        TexMexVector vector = new TexMexVector(index, getVectorDimension());
        for (int i = 0; i < getVectorDimension(); i++) {
            float value = getFieldValueByIndex(components, i);
            vector.setEntry(i, value);
        }
        index++;
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
