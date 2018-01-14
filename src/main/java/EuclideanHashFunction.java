import org.apache.commons.math3.linear.ArrayRealVector;

import java.util.Random;
import java.util.function.Function;

public class EuclideanHashFunction implements Function<ArrayRealVector, Integer> {
    private final double w;
    private final int vectorDimension;
    private final double offset;

    public EuclideanHashFunction(double w, int vectorDimension) {
        this.w = w;
        this.vectorDimension = vectorDimension;
        this.offset = calcOffset(w);

    }

    public Integer apply(ArrayRealVector vector) {
        return null;
    }

    private static double calcOffset(double w) {
        return new Random().nextDouble() * w;
    }
}
