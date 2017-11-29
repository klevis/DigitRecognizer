package ramo.klevis.ui;

import com.mortennobel.imagescaling.ResampleFilters;
import com.mortennobel.imagescaling.ResampleOp;
import ramo.klevis.data.LabeledImage;
import ramo.klevis.nn.NeuralNetwork;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.plaf.FontUIResource;

public class UI {

    private static final int FRAME_WIDTH = 1200;
    private static final int FRAME_HEIGHT = 628;
    private final NeuralNetwork neuralNetwork = new NeuralNetwork();

    private DrawArea drawArea;
    private JFrame mainFrame;
    private JPanel mainPanel;
    private JPanel drawAndDigitPredictionPanel;
    private SpinnerNumberModel modelTrainSize;
    private JSpinner trainField;
    private int TRAIN_SIZE = 30000;
    private final Font sansSerifBold = new Font("SansSerif", Font.BOLD, 18);
    private final Font sansSerifItalic = new Font("SansSerif", Font.ITALIC, 18);
    private int TEST_SIZE = 10000;
    private SpinnerNumberModel modelTestSize;
    private JSpinner testField;

    public UI() throws Exception {
        UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        UIManager.put("Button.font", new FontUIResource(new Font("Dialog", Font.BOLD, 18)));
        UIManager.put("ProgressBar.font", new FontUIResource(new Font("Dialog", Font.BOLD, 18)));
    }

    public void initUI() {
        // create main frame
        mainFrame = createMainFrame();

        mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout());

        JPanel topPanel = new JPanel(new FlowLayout());
        JButton recognize = new JButton("Recognize Digit");
        JButton train = new JButton("Train NN");
        train.addActionListener(e -> {
            try {
                neuralNetwork.train((Integer) trainField.getValue(), (Integer) testField.getValue());
            } catch (IOException e1) {
                throw new RuntimeException(e1);
            }
        });
        recognize.addActionListener(e -> {

            Image drawImage = drawArea.getImage();
            BufferedImage sbi = toBufferedImage(drawImage);

            Image scaled = scale(sbi);
            try {
                ImageIO.write(toBufferedImage(drawImage), "jpg", new File("img2.jpg"));
            } catch (IOException e1) {
                throw new RuntimeException(e1);
            }
            try {
                ImageIO.write(toBufferedImage(scaled), "png", new File("img.png"));
            } catch (IOException e1) {
                throw new RuntimeException(e1);
            }
            BufferedImage scaledReady = toBufferedImage(scaled);

            double[] pixels = transformImageToOneDimensionalVector(scaledReady);
            debug(pixels);
            try {
                BufferedImage read = ImageIO.read(new File("lale" + ".png"));
                double[] doubles = transformImageToOneDimensionalVector(read);
                LabeledImage labeledImage = new LabeledImage(0, doubles);
                neuralNetwork.init();
                LabeledImage predict = neuralNetwork.predict(labeledImage);
                System.out.println("predict = " + predict);
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        });
        topPanel.add(recognize);
        topPanel.add(train);
        JButton clear = new JButton("Clear");
        topPanel.add(clear);
        clear.addActionListener(e -> {
            drawArea.setImage(null);
            drawArea.repaint();
            drawAndDigitPredictionPanel.updateUI();
        });
        JLabel tL = new JLabel("Training Data");
        tL.setFont(sansSerifBold);
        topPanel.add(tL);
        modelTrainSize = new SpinnerNumberModel(TRAIN_SIZE, 10000, 60000, 1000);
        trainField = new JSpinner(modelTrainSize);
        trainField.setFont(sansSerifBold);
        topPanel.add(trainField);

        JLabel ttL = new JLabel("Test Data");
        ttL.setFont(sansSerifBold);
        topPanel.add(ttL);
        modelTestSize = new SpinnerNumberModel(TEST_SIZE, 1000, 10000, 500);
        testField = new JSpinner(modelTestSize);
        testField.setFont(sansSerifBold);
        topPanel.add(testField);

        mainPanel.add(topPanel, BorderLayout.NORTH);

        drawAndDigitPredictionPanel = new JPanel(new GridLayout());
        drawArea = new DrawArea();
        DrawArea drawArea2 = new DrawArea();

        drawAndDigitPredictionPanel.add(drawArea);
        drawAndDigitPredictionPanel.add(drawArea2);
        mainPanel.add(drawAndDigitPredictionPanel, BorderLayout.CENTER);

        addSignature();

        mainFrame.add(mainPanel);
        mainFrame.setVisible(true);

    }

    public static void debug(double[] pixels) {
        int[] imgPixels = new int[pixels.length];
        BufferedImage image = new BufferedImage(28, 28, BufferedImage.TYPE_INT_ARGB);
        int p = 0;
        for (double pixel : pixels) {
            int gray = (int) (255 - pixel);
            imgPixels[p] = 0xFF000000 | (gray << 16) | (gray << 8) | gray;
            p++;
        }
        image.setRGB(0, 0, 28, 28, imgPixels, 0, 28);
        File outputfile = new File("lale" + ".png");

        try {
            ImageIO.write(image, "png", outputfile);
        } catch (IOException e1) {
            e1.printStackTrace();
        }
    }

    public static BufferedImage scale(BufferedImage imageToScale) {
        ResampleOp resizeOp = new ResampleOp(28, 28);
        resizeOp.setFilter(ResampleFilters.getLanczos3Filter());
        BufferedImage filter = resizeOp.filter(imageToScale, null);
        return filter;
    }

    public static BufferedImage toBufferedImage(Image img) {
        // Create a buffered image with transparency
        BufferedImage bimage = new BufferedImage(img.getWidth(null), img.getHeight(null), BufferedImage.TYPE_INT_ARGB);

        // Draw the image on to the buffered image
        Graphics2D bGr = bimage.createGraphics();
        bGr.drawImage(img, 0, 0, null);
        bGr.dispose();

        // Return the buffered image
        return bimage;
    }


    public static double[] transformImageToOneDimensionalVector(BufferedImage img) {

        double[] imageGray = new double[28 * 28];
        int w = img.getWidth();
        int h = img.getHeight();
        int index = 0;
        for (int i = 0; i < w; i++) {
            for (int j = 0; j < h; j++) {
                Color color = new Color(img.getRGB(j, i), true);
                int red = (color.getRed());
                int green = (color.getGreen());
                int blue = (color.getBlue());
                double v = 255 - (red + green + blue) / 3d;
                imageGray[index] = v;
                index++;
            }
        }
        return imageGray;
    }


    private JFrame createMainFrame() {
        JFrame mainFrame = new JFrame();
        mainFrame.setTitle("Digit Recognizer");
        mainFrame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        mainFrame.setSize(FRAME_WIDTH, FRAME_HEIGHT);
        mainFrame.setLocationRelativeTo(null);
        mainFrame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosed(WindowEvent e) {
                System.exit(0);
            }
        });
        ImageIcon imageIcon = new ImageIcon("icon.png");
        mainFrame.setIconImage(imageIcon.getImage());

        return mainFrame;
    }

    private void addSignature() {
        JLabel signature = new JLabel("ramok.tech", JLabel.HORIZONTAL);
        signature.setFont(new Font(Font.SANS_SERIF, Font.ITALIC, 20));
        signature.setForeground(Color.BLUE);
        mainPanel.add(signature, BorderLayout.SOUTH);
    }

}