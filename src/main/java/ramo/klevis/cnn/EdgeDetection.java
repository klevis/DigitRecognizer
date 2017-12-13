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

    private static final double[][] FILTER_VERTICAL = {{1, 0, -1}, {1, 0, -1}, {1, 0, -1}};
    private static final double[][] FILTER_HORIZONTAL = {{1, 1, 1}, {0, 0, 0}, {-1, -1, -1}};
    private static final double[][] FILTER_SOBEL = {{1, 0, -1}, {2, 0, -2}, {1, 0, -1}};
    private static final String INPUT_IMAGE = "resources/smallGirl.png";
    private static int count = 1;

    public static void main(String[] args) throws IOException {
        detectVerticalEdges();
        detectHorizontalEdges();
        detectSobelEdges();
    }

    private static void detectSobelEdges() throws IOException {
        BufferedImage bufferedImage = ImageIO.read(new File(INPUT_IMAGE));

        double[][][] image = transformImageToArray(bufferedImage);
        double[][] finalConv = applyConvolution(bufferedImage.getWidth(), bufferedImage.getHeight(), image, FILTER_SOBEL);
        reCreateOriginalImageFromMatrix(bufferedImage, finalConv);
    }

    private static void detectHorizontalEdges() throws IOException {
        BufferedImage bufferedImage = ImageIO.read(new File(INPUT_IMAGE));

        double[][][] image = transformImageToArray(bufferedImage);
        double[][] finalConv = applyConvolution(bufferedImage.getWidth(), bufferedImage.getHeight(), image, FILTER_HORIZONTAL);
        reCreateOriginalImageFromMatrix(bufferedImage, finalConv);
    }


    private static void detectVerticalEdges() throws IOException {
        BufferedImage bufferedImage = ImageIO.read(new File(INPUT_IMAGE));
        double[][][] image = transformImageToArray(bufferedImage);
        double[][] finalConv = applyConvolution(bufferedImage.getWidth(), bufferedImage.getHeight(), image, FILTER_VERTICAL);
        reCreateOriginalImageFromMatrix(bufferedImage, finalConv);
    }

    private static double[][][] transformImageToArray(BufferedImage bufferedImage) {
        int width = bufferedImage.getWidth();
        int height = bufferedImage.getHeight();
        return transformImageToArray(bufferedImage, width, height);
    }

    private static double[][] applyConvolution(int width, int height, double[][][] image, double[][] filter) {
        Convolution convolution = new Convolution();
        double[][] redConv = convolution.convolutionType2(image[0], height, width, filter, 3, 3, 1);
        double[][] greenConv = convolution.convolutionType2(image[1], height, width, filter, 3, 3, 1);
        double[][] blueConv = convolution.convolutionType2(image[2], height, width, filter, 3, 3, 1);
        double[][] finalConv = new double[redConv.length][redConv[0].length];
        for (int i = 0; i < redConv.length; i++) {
            for (int j = 0; j < redConv[i].length; j++) {
                finalConv[i][j] = redConv[i][j] + greenConv[i][j] + blueConv[i][j];
            }
        }
        return finalConv;
    }

    private static double[][][] transformImageToArray(BufferedImage bufferedImage, int width, int height) {
        double[][][] image = new double[3][height][width];
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                Color color = new Color(bufferedImage.getRGB(j, i));
                image[0][i][j] = color.getRed();
                image[1][i][j] = color.getGreen();
                image[2][i][j] = color.getBlue();
            }
        }
        return image;
    }

    private static void reCreateOriginalImageFromMatrix(BufferedImage originalImage, double[][] imageRGB) throws IOException {
        BufferedImage writeBackImage = new BufferedImage(originalImage.getWidth(), originalImage.getHeight(), BufferedImage.TYPE_INT_RGB);
        for (int i = 0; i < imageRGB.length; i++) {
            for (int j = 0; j < imageRGB[i].length; j++) {
                Color color = new Color(fixOutOfRangeRGBValues(imageRGB[i][j]),
                        fixOutOfRangeRGBValues(imageRGB[i][j]),
                        fixOutOfRangeRGBValues(imageRGB[i][j]));
                writeBackImage.setRGB(j, i, color.getRGB());
            }
        }
        File outputFile = new File("edges" + count++ + ".png");
        ImageIO.write(writeBackImage, "png", outputFile);
    }

    private static int fixOutOfRangeRGBValues(double value) {
        if (value < 0.0) {
            value = -value;
        }
        if (value > 255) {
            return 255;
        } else {
            return (int) value;
        }
    }
}
