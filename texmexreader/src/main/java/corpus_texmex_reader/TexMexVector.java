package corpus_texmex_reader;

import org.apache.commons.math3.linear.ArrayRealVector;

import java.io.Serializable;

public class TexMexVector extends ArrayRealVector implements Serializable {
    private final int index;

    public TexMexVector(int index, int size) {
        super(size);
        this.index = index;
    }

    public int getIndex() {
        return index;
    }
}
