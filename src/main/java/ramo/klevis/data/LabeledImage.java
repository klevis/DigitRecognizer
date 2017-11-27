package ramo.klevis.data;

import org.apache.spark.ml.linalg.Vector;
import org.apache.spark.ml.linalg.Vectors;
import scala.collection.immutable.List;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Created by klevis.ramo on 11/27/2017.
 */
public class LabeledImage implements Serializable {
    private double label;
    private Vector features;

    public LabeledImage(int label, double[] pixels) {
        this.label = label;
        java.util.List<Integer> indexes = new ArrayList<>();
        int index = 0;
        for (double pixel : pixels) {
            if (pixel != 0.0d) {
                indexes.add(index);
            }
            index++;
        }
        Vector sparse = Vectors.sparse(pixels.length, indexes.stream().mapToInt(i -> i).toArray(), pixels);
        features = Vectors.dense(pixels);
    }

    public Vector getFeatures() {
        return features;
    }

    public double getLabel() {
        return label;
    }

    public void setLabel(double label) {
        this.label = label;
    }
}
