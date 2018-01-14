import org.apache.commons.math3.linear.ArrayRealVector;

import java.util.Random;
import java.util.function.Function;

public class EuclideanHashFunction implements Function<ArrayRealVector, Integer> {
    private final double w;
    private final int vectorDimension;
    private final double offset;
    private final ArrayRealVector randomProjection;

    public EuclideanHashFunction(double w, int vectorDimension) {
        this.w = w;
        this.vectorDimension = vectorDimension;
        this.offset = calcOffset(w);
        this.randomProjection = calcRandomProjection();
    }

    public Integer apply(ArrayRealVector vector) {
        return (int) Math.floor((vector.dotProduct(randomProjection) + offset) / w);
    }

    private ArrayRealVector calcRandomProjection() {
        ArrayRealVector projection = new ArrayRealVector(vectorDimension);
        Random rand = new Random();
        for (int i = 0; i < vectorDimension; i++) {
            projection.setEntry(i, rand.nextGaussian());
        }

        return projection;
    }

    private static double calcOffset(double w) {
        return new Random().nextDouble() * w;
    }
}
