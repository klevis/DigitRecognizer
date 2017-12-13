package digit.recogniser.data;

import org.junit.Test;
import ramo.klevis.data.IdxReader;
import ramo.klevis.data.LabeledImage;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;

import static org.junit.Assert.assertTrue;
import static ramo.klevis.data.IdxReader.INPUT_IMAGE_PATH;
import static ramo.klevis.data.IdxReader.INPUT_LABEL_PATH;

/**
 * @author (created on 12 / 7 / 2017).
 */
public class IdxReaderTest {

    @Test //small amount of data
    public void loadDataOne_Thousand() {
        List<LabeledImage> labeledImages = IdxReader.loadData(100);
    }

    /**
     * see the pdf with description of a data set
     */
    @Test
    public void testmagicNumbersForBigDataset() {
        try (FileInputStream inImage = new FileInputStream(INPUT_IMAGE_PATH);
             FileInputStream inLabel = new FileInputStream(INPUT_LABEL_PATH)) {

            //==========================================================================================================
            // it reads the next byte of data (8 bits) then put them to left side of an int
            // so the int is 32 bit is fully filled at the end of logic
            // and it moves the cursor to a position after first 32 bits (4 bytes)
            System.out.println("Available bytes before read: " + inImage.available());//47040016
            int magicNumberImages = (inImage.read() << 24) | (inImage.read() << 16) | (inImage.read() << 8) | (inImage.read());
            int numberOfImages = (inImage.read() << 24) | (inImage.read() << 16) | (inImage.read() << 8) | (inImage.read());
            int numberOfRows = (inImage.read() << 24) | (inImage.read() << 16) | (inImage.read() << 8) | (inImage.read());
            int numberOfColumns = (inImage.read() << 24) | (inImage.read() << 16) | (inImage.read() << 8) | (inImage.read());

            assertTrue(magicNumberImages == 2051);
            assertTrue(numberOfImages == 60000);
            assertTrue(numberOfRows == 28);
            assertTrue(numberOfColumns == 28);

            //the same as inImage.skip(16);
            System.out.println("Available bytes after read: " + inImage.available());//47040000

            //==========================================================================================================
            // it reads the next byte of data (8 bits) then put them to left side of an int
            // so the int is 32 bit is fully filled at the end of logic
            // and it moves the cursor to a position after first 32 bits (4 bytes)
            System.out.println("Available bytes before read: " + inLabel.available()); //60008
            int magicNumberLabels = (inLabel.read() << 24) | (inLabel.read() << 16) | (inLabel.read() << 8) | (inLabel.read());
            int numberOfLabels = (inLabel.read() << 24) | (inLabel.read() << 16) | (inLabel.read() << 8) | (inLabel.read());
            assertTrue(magicNumberLabels == 2049);
            assertTrue(numberOfLabels == 60000);

            System.out.println("Available bytes after read: " + inLabel.available()); //60000
            //the same as inLabel.skip(8);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}