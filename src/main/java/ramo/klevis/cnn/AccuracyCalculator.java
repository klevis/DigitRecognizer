package ramo.klevis.cnn;

import org.deeplearning4j.datasets.iterator.impl.MnistDataSetIterator;
import org.deeplearning4j.earlystopping.scorecalc.ScoreCalculator;
import org.deeplearning4j.eval.Evaluation;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.nd4j.linalg.dataset.api.iterator.DataSetIterator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by klevis.ramo on 12/13/2017.
 */
public class AccuracyCalculator implements ScoreCalculator<MultiLayerNetwork> {

    private static final Logger log = LoggerFactory.getLogger(AccuracyCalculator.class);

    private final MnistDataSetIterator dataSetIterator;

    public AccuracyCalculator(MnistDataSetIterator dataSetIterator) {
        this.dataSetIterator = dataSetIterator;
    }

    int i = 0;

    @Override
    public double calculateScore(MultiLayerNetwork network) {
        Evaluation evaluate = network.evaluate(dataSetIterator);
        double accuracy = evaluate.accuracy();
        log.error("Accuracy " + i++ + " " + accuracy);
        return 1 - evaluate.accuracy();
    }
}
