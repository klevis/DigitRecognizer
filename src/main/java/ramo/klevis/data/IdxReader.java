package ramo.klevis.data;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class IdxReader {

    private final static Logger LOGGER = LoggerFactory.getLogger(IdxReader.class);

    public java.util.List<LabeledImage> loadData(int size) throws IOException {

        String inputImagePath = "resources/train-images.idx3-ubyte";
        String inputLabelPath = "resources/train-labels.idx1-ubyte";

        return getLabeledImages(inputImagePath, inputLabelPath, size);
    }

    private List<LabeledImage> getLabeledImages(String inputImagePath, String inputLabelPath, int number) throws IOException {
        FileInputStream inLabel = null;
        FileInputStream inImage = null;

        try {
            inImage = new FileInputStream(inputImagePath);
            inLabel = new FileInputStream(inputLabelPath);

            int magicNumberImages = (inImage.read() << 24) | (inImage.read() << 16) | (inImage.read() << 8) | (inImage.read());
            int numberOfImages = (inImage.read() << 24) | (inImage.read() << 16) | (inImage.read() << 8) | (inImage.read());
            int numberOfRows = (inImage.read() << 24) | (inImage.read() << 16) | (inImage.read() << 8) | (inImage.read());
            int numberOfColumns = (inImage.read() << 24) | (inImage.read() << 16) | (inImage.read() << 8) | (inImage.read());

            int magicNumberLabels = (inLabel.read() << 24) | (inLabel.read() << 16) | (inLabel.read() << 8) | (inLabel.read());
            int numberOfLabels = (inLabel.read() << 24) | (inLabel.read() << 16) | (inLabel.read() << 8) | (inLabel.read());

            int numberOfPixels = numberOfRows * numberOfColumns;
            double[] imgPixels = new double[numberOfPixels];
            List<LabeledImage> all = new ArrayList();

            long start = System.currentTimeMillis();
            for (int i = 0; i < number; i++) {

                if (i % 1000 == 0) {
                    LOGGER.info("Number of images extracted: " + i);
                }

                for (int p = 0; p < numberOfPixels; p++) {
                    imgPixels[p] = inImage.read();
                }

                int label = inLabel.read();
                all.add(new LabeledImage(label, imgPixels));
            }
            LOGGER.info("Time in seconds" + ((System.currentTimeMillis() - start) / 1000d));
            return all;

        } finally {
            if (inImage != null) {
                inImage.close();
            }
            if (inLabel != null) {
                inLabel.close();
            }
        }
    }

    public java.util.List<LabeledImage> loadTestData(int size) throws IOException {

        String inputImagePath = "resources/t10k-images.idx3-ubyte";
        String inputLabelPath = "resources/t10k-labels.idx1-ubyte";

        return getLabeledImages(inputImagePath, inputLabelPath, size);
    }


}