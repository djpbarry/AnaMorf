/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package AnaMorf;

import ij.IJ;
import ij.ImagePlus;
import ij.WindowManager;
import ij.gui.GenericDialog;
import ij.process.ByteProcessor;
import ij.process.ByteStatistics;
import ij.process.ColorProcessor;
import ij.process.ImageProcessor;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.io.File;
import javax.swing.JFileChooser;

/**
 *
 * @author barry05
 */
public class Utilities {

    /**
     * Attempts to produce a high-contrast greyscale image from an RGB image.
     *
     * @return a {@link ByteProcessor} containing whichever channel of the input
     * RGB image exhibits the greatest contrast (measured by comparing the
     * standard deviation of intensity levels within the three different bands).
     */
    public static ByteProcessor getHighContrastGreyImage(ColorProcessor image) {
        int w = image.getWidth(), h = image.getHeight();
        int size = w * h;
        byte redPix[] = new byte[size], greenPix[] = new byte[size],
                bluePix[] = new byte[size];

        image.getRGB(redPix, greenPix, bluePix);

        ByteProcessor redProcessor = new ByteProcessor(w, h, redPix, null);
        ByteProcessor greenProcessor = new ByteProcessor(w, h, greenPix, null);
        ByteProcessor blueProcessor = new ByteProcessor(w, h, bluePix, null);

        double redSD = (new ByteStatistics((ImageProcessor) redProcessor)).stdDev;
        double greenSD = (new ByteStatistics((ImageProcessor) greenProcessor)).stdDev;
        double blueSD = (new ByteStatistics((ImageProcessor) blueProcessor)).stdDev;

        if (redSD > greenSD && redSD > blueSD) {
            return redProcessor;
        } else if (greenSD > blueSD) {
            return greenProcessor;
        } else {
            return blueProcessor;
        }
    }

    /**
     * Opens a dialog for the user to specify the working directory
     *
     * @return the directory in which the images to be analysed are located
     */
    public static File getFolder(File currentDirectory, String title) {
        boolean validDirectory = false;
        File newDirectory = null;
        if (title == null) {
            title = "Select Directory";
        }

        while (!validDirectory) {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setDialogTitle(title);
            fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            fileChooser.setApproveButtonText("Ok");

            if (currentDirectory != null) {
                fileChooser.setCurrentDirectory(currentDirectory);
            }

            int result = fileChooser.showOpenDialog(null);
            if (result == JFileChooser.CANCEL_OPTION) {
                Toolkit.getDefaultToolkit().beep();
                if (IJ.showMessageWithCancel("Exit", "Do you wish to exit?")) {
                    return null;
                }
            } else {
                newDirectory = fileChooser.getSelectedFile();
                if (!(newDirectory.isDirectory() && newDirectory.exists())) {
                    IJ.showMessage("Invalid Directory!");
                    validDirectory = false;
                } else {
                    validDirectory = true;
                }
            }
        }
        return newDirectory;
    }

    /**
     * Compares two {@link Rectangle} objects to determine if any of their sides
     * are coincident.
     *
     * @param rect1 first rectangle
     * @param rect2 second rectangle
     * @return true if the two rectangles have a common side, false otherwise.
     */
    public static boolean checkBounds(Rectangle rect1, Rectangle rect2) {
        int area1 = rect1.width * rect1.height;
        int area2 = rect2.width * rect2.height;

        if (area2 < area1) {
            Rectangle temp = new Rectangle(rect1.x, rect1.y, rect1.width, rect1.height);
            rect1 = new Rectangle(rect2.x, rect2.y, rect2.width, rect2.height);
            rect2 = new Rectangle(temp.x, temp.y, temp.width, temp.height);
        }

        int x1 = rect1.x, y1 = rect1.y, x2 = rect2.x, y2 = rect2.y;
        int width1 = rect1.width, width2 = rect2.width;
        int height1 = rect1.height, height2 = rect2.height;

        if (((x1 == x2) || ((x1 + width1) == (x2 + width2))) && (y1 >= y2)
                && (y1 <= y2 + height2)) {
            return true;
        } else if (((y1 == y2) || ((y1 + height1) == (y2 + height2))) && (x1 >= x2)
                && (x1 <= x2 + width2)) {
            return true;
        }

        return false;
    }

    public static ImagePlus[] specifyImages(String choice1, String choice2) {
        int windIDs[] = WindowManager.getIDList();
        String winTitles[] = new String[windIDs.length + 1];
        for (int i = 0; i < windIDs.length; i++) {
            winTitles[i] = WindowManager.getImage(windIDs[i]).getTitle();
        }
        winTitles[windIDs.length] = "None";
        GenericDialog gd = new GenericDialog("Specify Inputs");
        gd.addChoice(choice1, winTitles, winTitles[0]);
        gd.addChoice(choice2, winTitles, winTitles[1]);
        gd.showDialog();
        ImagePlus output[] = new ImagePlus[2];
        output[0] = WindowManager.getImage(windIDs[gd.getNextChoiceIndex()]);
        int sigChoice = gd.getNextChoiceIndex();
        if (sigChoice >= windIDs.length) {
            output[1] = null;
        } else {
            output[1] = WindowManager.getImage(windIDs[sigChoice]);
        }
        return output;
    }

    public static int checkRange(int number, int lowerBound, int upperBound) {
        int newNo = number;
        if (newNo < lowerBound) {
            newNo += upperBound;
        }
        if (newNo >= upperBound) {
            newNo -= upperBound;
        }
        return newNo;
    }
}
