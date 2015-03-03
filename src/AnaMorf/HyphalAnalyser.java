package AnaMorf;

import IAClasses.SkeletonProcessor;
import ij.process.ColorProcessor;
import ij.process.ImageProcessor;
import java.awt.Color;
import java.awt.Rectangle;

/**
 * Determines the total length (<i>L<sub>t</sub></i>) of a hyphal structure (a binary skeleton),
 * the total number of hyphal tips (<i>N</i>) and the hyphal growth unit (<i>L<sub>t</sub>/N</i>).
 * @author   David J Barry <davejpbarry@gmail.com>
 * @version  05OCT2010
 */
public class HyphalAnalyser {

    private ImageProcessor processor;
    private double hyphalGrowthUnit = 0;
    private int hyphalLength = 0, tips = 0, branchpoints = 0, radius;
    private ImageProcessor output;
    private Rectangle imageBounds, objBounds;

    public HyphalAnalyser(ImageProcessor image, double res, Rectangle imageBox,
            Rectangle objBox) {
        this.processor = image;
        imageBounds = imageBox;
        objBounds = objBox;
//        radius = (int) Math.round(4.0 * 1.12347 / res);
        radius = 2;
        output = new ColorProcessor(imageBox.width, imageBox.height);
        output.setLineWidth(radius / 2);
        output.setColor(Color.black);
        output.fill();
    }

    /**
     * Analyses the skeletal structure contained in <i>image</i> and determines
     * the total length, the number of end-points and the ratio of total length
     * to end-points.
     */
    public void analyse() {
        int x, y, foreground = 0, background = 255, diam = 2 * radius + 1, imageX, imageY;
        output.setColor(Color.white);

        if (processor.isInvertedLut()) {
            foreground = 255;
            background = 0;
        }

        int width = processor.getWidth();
        int height = processor.getHeight();

        for (y = 0; y < height; y++) {
            for (x = 0; x < width; x++) {
                if (processor.getPixel(x, y) == foreground) {
                    hyphalLength++;
                    imageX = x + objBounds.x;
                    imageY = y + objBounds.y;
                    output.drawPixel(imageX, imageY);
                    if (SkeletonProcessor.isEndPoint(x, y, processor, background)) {
                        if ((imageX > radius)
                                && (imageX < imageBounds.width - radius)
                                && (imageY > radius)
                                && (imageY < imageBounds.height - radius)) {
                            output.setColor(Color.red);
                            output.drawOval(imageX - radius, imageY - radius, diam, diam);
                            output.setColor(Color.white);
                            tips++;
                        }
                    } else if (searchNeighbourhood(x, y, 1, foreground) > 2) {
                        if (SkeletonProcessor.isBranchPoint(x, y, processor, foreground) == 0) {
                            output.setColor(Color.yellow);
                            output.drawOval(imageX - radius, imageY - radius, diam, diam);
                            output.setColor(Color.white);
                            branchpoints++;
                        }
                    }
                }
            }
        }
        if (tips > 0) {
            hyphalGrowthUnit = (double) hyphalLength / tips;
        }
    }

    public int searchNeighbourhood(int x, int y, int radius, int value) {
        int count = 0;

        for (int i = x - radius; i <= x + radius; i++) {
            for (int j = y - radius; j <= y + radius; j++) {
                if ((processor.getPixel(i, j) == value) && !((x == i) && (y == j))) {
                    count++;
                }
            }
        }
        return count;
    }

    /**
     * @return the hyphal growth unit (total length / number of tips).
     */
    public double getHGU() {
        return hyphalGrowthUnit;
    }

    /**
     * @return the total hyphal length in pixels.
     */
    public int getLength() {
        return hyphalLength;
    }

    /**
     * @return the total number of hyphal tips.
     */
    public int getTips() {
        return tips;
    }

    public int getBranchpoints() {
        return branchpoints;
    }

    public ImageProcessor getOutput() {
        return output;
    }
}
