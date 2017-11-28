package ramo.klevis.data;

import org.apache.spark.ml.linalg.Vector;
import org.apache.spark.ml.linalg.Vectors;

import java.io.Serializable;

/**
 * Created by klevis.ramo on 11/27/2017.
 */
public class LabeledImage implements Serializable {
    private double label;
    private Vector features;

    public LabeledImage(int label, double[] pixels) {
        this.label = label;
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

    @Override
    public String toString() {
        return "LabeledImage{" +
                "label=" + label +
                '}';
    }
}
