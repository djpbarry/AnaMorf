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
import ij.process.ByteStatistics;
import ij.process.ImageProcessor;
import java.awt.Rectangle;

/**
 * SkeletonPruner takes as argument a <i>ByteProcessor</i>, containing a binary
 * skeleton object. This image is processed to remove artifactual points (points
 * that may be removed without creating a 'break' in the structure) and
 * 'branches' less than a specified <i>minimumLength</i>.
 */
public class SkeletonPruner {

    private ByteProcessor outputProcessor = null;
    private final int FOREGROUND, BACKGROUND, MIN_BRANCH_LENGTH;

    public SkeletonPruner(int minimumLength, ByteProcessor inputProcessor) {
        MIN_BRANCH_LENGTH = minimumLength;
        int histogram[];
        int width = inputProcessor.getWidth();
        int height = inputProcessor.getHeight();

        if (inputProcessor.isInvertedLut()) {
            FOREGROUND = 255;
            BACKGROUND = 0;
        } else {
            FOREGROUND = 0;
            BACKGROUND = 255;
        }

        histogram = inputProcessor.getHistogram();
        /*
         * Non-binary images are not processed
         */
        if (histogram[0] + histogram[255] == width * height) {
            ByteProcessor referenceProcessor = (ByteProcessor) inputProcessor.duplicate();
            outputProcessor = (ByteProcessor) inputProcessor.duplicate();
            prunePoints(inputProcessor.getRoi(), referenceProcessor);
            /*
             * The image is repeatedly processed until no further changes are
             * necessary.
             */
            while (pruneBranches(referenceProcessor)) ;
            outputProcessor.setPixels(referenceProcessor.getPixels());
        }
    }

    /**
     * Removes all artifactual points from a skeleton structure.
     *
     * @param roi region of interest
     * @param processor image containing the skeleton structure, which is
     * modified during the operation.
     */
    public void prunePoints(Rectangle roi, ImageProcessor processor) {
        int x, y;
        processor.setColor(BACKGROUND);

        for (y = roi.y; y < roi.y + roi.height; y++) {
            for (x = roi.x; x < roi.x + roi.width; x++) {
                if (processor.getPixelValue(x, y) == FOREGROUND) {
                    if (SkeletonProcessor.removePixel(x, y, processor, FOREGROUND, BACKGROUND)) {
                        processor.drawPixel(x, y);
                    }
                }
            }
        }
        return;
    }

    /**
     * Removes all branches from a skeleton structure that are less than the
     * specified minimum length.
     *
     * @param processor image containing the skeleton structure
     * @return true if at least one branch has been removed, false otherwise
     */
    public boolean pruneBranches(ImageProcessor processor) {
        int x, y, i, length;
        int width = processor.getWidth(), height = processor.getHeight();
        Rectangle region;
        ByteStatistics stats = new ByteStatistics(processor);
        int size = stats.histogram[FOREGROUND];
        int xPixels[] = new int[size];
        int yPixels[] = new int[size];
        boolean change = false;

        processor.setColor(BACKGROUND);
        for (y = 1; y < height-1; y++) {
            for (x = 1; x < width-1; x++) {
                /*
                 * Image scanned until foreground pixel located
                 */
                if (processor.getPixelValue(x, y) == FOREGROUND) {
                    /*
                     * Tracing of branches commences from end-points only
                     */
                    if (SkeletonProcessor.isEndPoint(x, y, processor, BACKGROUND)) {
                        length = 0;
                        xPixels[length] = x;
                        yPixels[length] = y;
                        /*
                         * Tracing of the skeleton proceeds until the end of the
                         * current branch is reached
                         */
                        do {
                            length++;
                        } while (SkeletonProcessor.getNextPixel(xPixels, yPixels, processor, length, FOREGROUND));
                        if (length >= size) {
                            return change;
                        }
                        if (length < MIN_BRANCH_LENGTH) {
                            /*
                             * If a branch-point was located, the branch-point
                             * is not removed with the branch
                             */
                            if (length >= 0 && xPixels[length] == SkeletonProcessor.BRANCH
                                    && yPixels[length] == SkeletonProcessor.BRANCH) {
                                length--;
                            } /*
                             * If the path has 'double-backed', it is assumed
                             * a branch-point has been missed. A number of pixels
                             * are removed from the path to ensure a branch-point
                             * is not inadvertently removed (potentially causing
                             * a break in the skeleton).
                             */ else if ((xPixels[length] < SkeletonProcessor.BRANCH)
                                    && (yPixels[length] < SkeletonProcessor.BRANCH)) {
                                length += xPixels[length];
                            }
                            for (i = length - 1; i >= 0; i--) {
                                processor.drawPixel(xPixels[i], yPixels[i]);
                            }
                            /*
                             * Any residual points are now removed from the
                             * neighbourhood of the branch-point.
                             */
                            if (length > 0) {
                                region = new Rectangle(xPixels[length - 1] - 1,
                                        yPixels[length - 1] - 1, 3, 3);
                                prunePoints(region, processor);
                            } else {
                                processor.drawPixel(xPixels[length],
                                        yPixels[length]);
                            }
                            change = true;
                        }
                    }
                }
            }
        }
        return change;
    }

    /**
     * @return the image containing the 'pruned' skeleton structure.
     */
    public ByteProcessor getPrunedImage() {
        return outputProcessor;
    }
}
