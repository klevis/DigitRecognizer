package ramo.klevis.nn;

/**
 * Created by klevis.ramo on 11/27/2017.
 */

import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.ml.classification.MultilayerPerceptronClassificationModel;
import org.apache.spark.ml.classification.MultilayerPerceptronClassifier;
import org.apache.spark.ml.evaluation.MulticlassClassificationEvaluator;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;
import ramo.klevis.data.IdxReader;
import ramo.klevis.data.LabeledImage;

import java.io.IOException;
import java.util.List;

public class NeuralNetwork {

    private SparkSession sparkSession;
    private IdxReader idxReader;
    private MultilayerPerceptronClassificationModel load;

    public void init() {
        initSparkSession();
        if (load == null) {
            load = MultilayerPerceptronClassificationModel.load("C:\\Users\\klevis.ramo\\Desktop\\ModelWith40000");
        }
    }

    public void train(Integer trainData, Integer testFieldValue) throws IOException {

        initSparkSession();

        idxReader = new IdxReader();
        List<LabeledImage> labeledImages = idxReader.loadData(trainData);
        List<LabeledImage> testLabeledImages = idxReader.loadTestData(testFieldValue);
        Dataset<Row> train = sparkSession.createDataFrame(labeledImages, LabeledImage.class).checkpoint();
        Dataset<Row> test = sparkSession.createDataFrame(testLabeledImages, LabeledImage.class).checkpoint();


// specify layers for the neural network:
// input layer of size 4 (features), two intermediate of size 5 and 4
// and output of size 3 (classes)
        int[] layers = new int[]{784, 5, 4, 10};

// create the trainer and set its parameters
        MultilayerPerceptronClassifier trainer = new MultilayerPerceptronClassifier()
                .setLayers(layers)
                .setBlockSize(128)
                .setSeed(1234L)
                .setMaxIter(100);

// train the model
        MultilayerPerceptronClassificationModel model = trainer.fit(train);

        model.save("C:\\Users\\klevis.ramo\\Desktop\\ModelWith" + trainData);
// compute accuracy on the test set
        Dataset<Row> result = model.transform(test);
        Dataset<Row> predictionAndLabels = result.select("prediction", "label");
        MulticlassClassificationEvaluator evaluator = new MulticlassClassificationEvaluator()
                .setMetricName("accuracy");

        System.out.println("Test set accuracy = " + evaluator.evaluate(predictionAndLabels));
    }

    private void initSparkSession() {
        if (sparkSession == null) {
            sparkSession = SparkSession.builder()
                    .master("local[*]")
                    .appName("Online Retailer")
                    .getOrCreate();
        }

        sparkSession.sparkContext().setCheckpointDir("checkPoint");
    }

    public LabeledImage predict(LabeledImage labeledImage) {
        double predict = load.predict(labeledImage.getFeatures());
        labeledImage.setLabel(predict);
        return labeledImage;
    }
}
