/* 
 * Copyright (C) 2015 David Barry <david.barry at cancer.org.uk>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package AnaMorf;

import IAClasses.SkeletonProcessor;
import ij.process.ByteProcessor;
import ij.process.ColorProcessor;
import ij.process.ImageProcessor;
import java.awt.Color;
import java.awt.Rectangle;

/**
 * Determines the total length (<i>L<sub>t</sub></i>) of a hyphal structure (a binary skeleton),
 * the total number of hyphal tips (<i>N</i>) and the hyphal growth unit (<i>L<sub>t</sub>/N</i>).
 */
public class HyphalAnalyser {

    private ImageProcessor processor;
    private double hyphalGrowthUnit = 0;
    private int hyphalLength = 0, tips = 0, branchpoints = 0, radius;
    private ImageProcessor colorOutput, bwOutput;
    private Rectangle imageBounds, objBounds;

    public HyphalAnalyser(ImageProcessor image, double res, Rectangle imageBox,
            Rectangle objBox) {
        this.processor = image;
        imageBounds = imageBox;
        objBounds = objBox;
//        radius = (int) Math.round(4.0 * 1.12347 / res);
        radius = 2;
        colorOutput = new ColorProcessor(imageBox.width, imageBox.height);
        colorOutput.setLineWidth(radius / 2);
        colorOutput.setColor(Color.black);
        colorOutput.fill();
        bwOutput = new ByteProcessor(imageBox.width, imageBox.height);
        bwOutput.setColor(Color.black);
        bwOutput.fill();
    }

    /**
     * Analyses the skeletal structure contained in <i>image</i> and determines
     * the total length, the number of end-points and the ratio of total length
     * to end-points.
     */
    public void analyse() {
        int x, y, foreground = 0, background = 255, diam = 2 * radius + 1, imageX, imageY;
        colorOutput.setColor(Color.white);
        bwOutput.setColor(Color.white);
        
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
                    colorOutput.drawPixel(imageX, imageY);
                    bwOutput.drawPixel(imageX, imageY);
                    if (SkeletonProcessor.isEndPoint(x, y, processor, background)) {
                        if ((imageX > radius)
                                && (imageX < imageBounds.width - radius)
                                && (imageY > radius)
                                && (imageY < imageBounds.height - radius)) {
                            colorOutput.setColor(Color.red);
                            colorOutput.drawOval(imageX - radius, imageY - radius, diam, diam);
                            colorOutput.setColor(Color.white);
                            tips++;
                        }
                    } else if (searchNeighbourhood(x, y, 1, foreground) > 2) {
                        if (SkeletonProcessor.isBranchPoint(x, y, processor, foreground) == 0) {
                            colorOutput.setColor(Color.yellow);
                            colorOutput.drawOval(imageX - radius, imageY - radius, diam, diam);
                            colorOutput.setColor(Color.white);
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

    public ImageProcessor getColorOutput() {
        return colorOutput;
    }
    
        public ImageProcessor getBWOutput() {
        return bwOutput;
    }
}
