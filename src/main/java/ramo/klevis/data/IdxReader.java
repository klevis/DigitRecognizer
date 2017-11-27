package ramo.klevis.data;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;

public class IdxReader {

    public static void main(String[] args) throws IOException {
        FileInputStream inImage = null;
        FileInputStream inLabel = null;

        String inputImagePath = "src/main/resources/train-images.idx3-ubyte";
        String inputLabelPath = "src/main/resources/train-labels.idx1-ubyte";

        String outputPath = "src/main/resources/";

        int[] hashMap = new int[10];

        try {
            inImage = new FileInputStream(inputImagePath);
            inLabel = new FileInputStream(inputLabelPath);

            int magicNumberImages = (inImage.read() << 24) | (inImage.read() << 16) | (inImage.read() << 8) | (inImage.read());
            int numberOfImages = (inImage.read() << 24) | (inImage.read() << 16) | (inImage.read() << 8) | (inImage.read());
            int numberOfRows = (inImage.read() << 24) | (inImage.read() << 16) | (inImage.read() << 8) | (inImage.read());
            int numberOfColumns = (inImage.read() << 24) | (inImage.read() << 16) | (inImage.read() << 8) | (inImage.read());

            int magicNumberLabels = (inLabel.read() << 24) | (inLabel.read() << 16) | (inLabel.read() << 8) | (inLabel.read());
            int numberOfLabels = (inLabel.read() << 24) | (inLabel.read() << 16) | (inLabel.read() << 8) | (inLabel.read());

            BufferedImage image = new BufferedImage(numberOfColumns, numberOfRows, BufferedImage.TYPE_INT_ARGB);
            int numberOfPixels = numberOfRows * numberOfColumns;
            int[] imgPixels = new int[numberOfPixels];
            HashMap<Integer, int[]> all = new HashMap<>();

            long start = System.currentTimeMillis();
            for (int i = 0; i < 20000; i++) {

                if (i % 1000 == 0) {
                    System.out.println("Number of images extracted: " + i);
                }

                for (int p = 0; p < numberOfPixels; p++) {
                    int gray = 255 - inImage.read();
                    imgPixels[p] = 0xFF000000 | (gray << 16) | (gray << 8) | gray;
                }

                image.setRGB(0, 0, numberOfColumns, numberOfRows, imgPixels, 0, numberOfColumns);

                int label = inLabel.read();

                hashMap[label]++;
                all.put(label, imgPixels);
//                File outputfile = new File(outputPath + label + "_0" + hashMap[label] + ".png");

//                ImageIO.write(image, "png", outputfile);
            }
            System.out.println("Time in seconds" + ((System.currentTimeMillis() - start) / 1000d));

        } finally {
            if (inImage != null) {
                inImage.close();
            }
            if (inLabel != null) {
                inLabel.close();
            }
        }
    }

}