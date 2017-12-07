package ramo.klevis.cnn;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

/**
 * Created by klevis.ramo on 12/7/2017.
 */
public class EdgeDetection {

    public static void main(String[] args) throws IOException {
        detectVerticalEdges();
    }

    private static void detectVerticalEdges() throws IOException {
        BufferedImage bufferedImage = ImageIO.read(new File("resources/smallGirl.png"));

        int width = bufferedImage.getWidth();
        int height = bufferedImage.getHeight();
        double[][][] image = new double[3][height][width];
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                Color color = new Color(bufferedImage.getRGB(j, i));
                image[0][i][j] = color.getRed();
                image[1][i][j] = color.getGreen();
                image[2][i][j] = color.getBlue();
            }
        }
        Convolution convolution = new Convolution();
        double[][] redConv = convolution.convolutionType1(image[0], height, width, new double[][]{{1, 0, -1}, {1, 0, -1}, {1, 0, -1}}, 3, 3, 1);
        double[][] greenConv = convolution.convolutionType1(image[1], height, width, new double[][]{{1, 0, -1}, {1, 0, -1}, {1, 0, -1}}, 3, 3, 1);
        double[][] blueConv = convolution.convolutionType1(image[2], height, width, new double[][]{{1, 0, -1}, {1, 0, -1}, {1, 0, -1}}, 3, 3, 1);
        double[][] finalConv = new double[redConv.length][redConv[0].length];
        for (int i = 0; i < redConv.length; i++) {
            for (int j = 0; j < redConv[i].length; j++) {
                finalConv[i][j] = redConv[i][j] + greenConv[i][j] + blueConv[i][j];
            }
        }
        reCreateOriginalImageFromMatrix(bufferedImage, finalConv);
    }

    private static void reCreateOriginalImageFromMatrix(BufferedImage originalImage, double[][] imageRGB) throws IOException {
        BufferedImage writeBackImage = new BufferedImage(originalImage.getWidth(), originalImage.getHeight(), BufferedImage.TYPE_INT_RGB);
        for (int i = 0; i < imageRGB.length; i++) {
            for (int j = 0; j < imageRGB[i].length; j++) {
                Color color = new Color(fixOutOfRangeValues(imageRGB[i][j]), fixOutOfRangeValues(imageRGB[i][j]), fixOutOfRangeValues(imageRGB[i][j]));
                writeBackImage.setRGB(j, i, color.getRGB());
            }
        }
        File outputFile = new File("edges.png");
        ImageIO.write(writeBackImage, "png", outputFile);
    }

    private static int fixOutOfRangeValues(double value) {
        if (value < 0) {
            return 0;
        } else if (value > 255) {
            return 255;
        } else {
            return (int) value;
        }
    }
}
