package ramo.klevis.data;

import org.apache.spark.ml.linalg.Vector;
import org.apache.spark.ml.linalg.Vectors;
import org.apache.spark.mllib.feature.Normalizer;

import java.io.Serializable;

/**
 * Created by klevis.ramo on 11/27/2017.
 */
public class LabeledImage implements Serializable {
    private double label;
    private Vector features;
    private final double[] pixelsNorm;

    public double[] getPixelsNorm() {
        return pixelsNorm;
    }

    public LabeledImage(int label, double[] pixels) {
        this.label = label;
//        Normalizer normalizer = new Normalizer();
//        org.apache.spark.mllib.linalg.Vector transform = normalizer.transform(org.apache.spark.mllib.linalg.Vectors.dense(pixels));
        pixelsNorm = normalizeFeatures(pixels);
        features = Vectors.dense(pixelsNorm);
    }

    private double[] normalizeFeatures(double[] pixels) {
        double min = Double.MAX_VALUE;
        double max = Double.MIN_VALUE;
        double sum = 0;
        for (double pixel : pixels) {
            sum = sum + pixel;
            if (pixel > max) {
                max = pixel;
            }
            if (pixel < min) {
                min = pixel;
            }
        }
        double mean = sum / pixels.length;

        double[] pixelsNorm = new double[pixels.length];
        for (int i = 0; i < pixels.length; i++) {
            pixelsNorm[i] = (pixels[i] - mean) / (max - min);
        }
        return pixelsNorm;
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
