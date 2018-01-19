package corpus_texmex_reader;

import org.apache.commons.math3.linear.ArrayRealVector;

public class TexMexVector extends ArrayRealVector {
    private final int index;

    public TexMexVector(int index, int size) {
        super(size);
        this.index = index;
    }

    public int getIndex() {
        return index;
    }
}
