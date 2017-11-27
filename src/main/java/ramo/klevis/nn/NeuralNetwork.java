package ramo.klevis.nn;

/**
 * Created by klevis.ramo on 11/27/2017.
 */

import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;
import org.apache.spark.ml.classification.MultilayerPerceptronClassificationModel;
import org.apache.spark.ml.classification.MultilayerPerceptronClassifier;
import org.apache.spark.ml.evaluation.MulticlassClassificationEvaluator;
import ramo.klevis.data.IdxReader;
import ramo.klevis.data.LabeledImage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class NeuralNetwork {

    private SparkSession sparkSession;

    public void train() throws IOException {

        initSparkSession();

        String path = "src/main/resources/a.txt";
        Dataset<Row> dataFrame1 = sparkSession.read().format("libsvm").load(path);

//// Load training data
//        String path = "data/mllib/sample_multiclass_classification_data.txt";
//        Dataset<Row> dataFrame = sparkSession.read().format("libsvm").load(path);
        List<LabeledImage> labeledImages = new IdxReader().loadData();
        Dataset<Row> dataFrame = sparkSession.createDataFrame(labeledImages, LabeledImage.class);


// Split the data into train and test
        Dataset<Row>[] splits = dataFrame.randomSplit(new double[]{0.6, 0.4}, 1234L);
        Dataset<Row> train = splits[0];
        Dataset<Row> test = splits[1];

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

// compute accuracy on the test set
        Dataset<Row> result = model.transform(test);
        Dataset<Row> predictionAndLabels = result.select("prediction", "label");
        MulticlassClassificationEvaluator evaluator = new MulticlassClassificationEvaluator()
                .setMetricName("accuracy");

        System.out.println("Test set accuracy = " + evaluator.evaluate(predictionAndLabels));
    }

    private JavaSparkContext createSparkContext() {
        SparkConf conf = new SparkConf().setAppName("Movie Recomender").setMaster("local[*]");
        return new JavaSparkContext(conf);
    }

    private void initSparkSession() {
        if (sparkSession == null) {
            sparkSession = SparkSession.builder()
                    .master("local[*]")
                    .appName("Online Retailer")
                    .getOrCreate();
        }
    }
}
