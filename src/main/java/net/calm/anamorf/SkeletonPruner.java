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
package net.calm.anamorf;

import ij.IJ;
import ij.ImagePlus;
import ij.WindowManager;
import ij.process.ByteProcessor;
import ij.process.ByteStatistics;
import ij.process.ImageProcessor;
import net.calm.iaclasslibrary.Graph.Node;
import net.calm.iaclasslibrary.IAClasses.SkeletonProcessor;

import java.awt.Rectangle;
import java.util.ArrayList;

/**
 * SkeletonPruner takes as argument a <i>ByteProcessor</i>, containing a binary
 * skeleton object. This image is processed to remove artifactual points (points
 * that may be removed without creating a 'break' in the structure) and
 * 'branches' less than a specified <i>minimumLength</i>.
 */
public class SkeletonPruner {

    private ByteProcessor outputProcessor = null;
    public static int FOREGROUND = 255, BACKGROUND = 0;
    private final int MIN_BRANCH_LENGTH;
    private ArrayList<int[][]> branches = new ArrayList();
    private Rectangle roi;
    private final boolean removeAll;
    private int index = 0;

    public SkeletonPruner(Rectangle roi) {
        this.roi = roi;
        MIN_BRANCH_LENGTH = 0;
        removeAll = false;
    }

    public static void prune(String minLength) {
        if (IJ.getInstance() == null) {
            return;
        }
        ImagePlus imp = WindowManager.getCurrentImage();
        if (imp == null) {
            return;
        }
        ImageProcessor ip = imp.getProcessor();
        if (!(ip instanceof ByteProcessor)) {
            return;
        }
        int min;
        try {
            min = Integer.parseInt(minLength);
        } catch (NumberFormatException e) {
            return;
        }
        SkeletonPruner sp = new SkeletonPruner(min, (ByteProcessor) ip);
        if (sp.getPrunedImage() != null) {
            (new ImagePlus(imp.getTitle() + "_Pruned", sp.getPrunedImage())).show();
        }
    }

    public SkeletonPruner(int minimumLength, ByteProcessor inputProcessor) {
        this(minimumLength, inputProcessor, null, false, false);
    }

    public SkeletonPruner(int minimumLength, ByteProcessor inputProcessor, Rectangle roi, boolean loops, boolean removeAll) {
        this.roi = roi;
        this.removeAll = removeAll;
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
            while (pruneBranches(referenceProcessor, false)) ;
            if (loops) {
                while (pruneBranches(referenceProcessor, true)) ;
            }
            outputProcessor.setPixels(referenceProcessor.getPixels());
        }
    }

    public void prunePoints(int x, int y, ImageProcessor processor) {
        prunePoints(new Rectangle(x - 1, y - 1, 3, 3), processor);
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
                        drawPixel(processor, x, y);
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
    public boolean pruneBranches(ImageProcessor processor, boolean loops) {
        int x, y, i, length;
        int width = processor.getWidth(), height = processor.getHeight();
        Rectangle region;
        ByteStatistics stats = new ByteStatistics(processor);
        int size = stats.histogram[FOREGROUND];
        short xPixels[] = new short[size];
        short yPixels[] = new short[size];
        boolean change = false;
        int rx = 0;
        int ry = 0;
        if (roi != null) {
            rx = roi.x;
            ry = roi.y;
        }

        processor.setColor(BACKGROUND);
        for (y = 1; y < height - 1; y++) {
            for (x = 1; x < width - 1; x++) {
                /*
                 * Image scanned until foreground pixel located
                 */
                if (processor.getPixelValue(x, y) == FOREGROUND) {
                    /*
                     * Tracing of branches commences from end-points only
                     */
                    if (loops || SkeletonProcessor.isEndPoint(x, y, processor, BACKGROUND)) {
                        length = 0;
                        xPixels[length] = (short) x;
                        yPixels[length] = (short) y;
                        /*
                         * Tracing of the skeleton proceeds until the end of the
                         * current branch is reached
                         */
                        do {
                            length++;
                        } while (SkeletonProcessor.getNextPixel(xPixels, yPixels, processor, length, FOREGROUND));
                        if (!removeAll && length >= size) {
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
                                drawPixel(processor, xPixels[i], yPixels[i]);
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
                                drawPixel(processor, xPixels[length],
                                        yPixels[length]);
                            }
                            change = true;
                        } else {
                            int[][] branchPix = new int[length][];
                            for (int j = 0; j < length; j++) {
                                branchPix[j] = new int[]{xPixels[j] + rx, yPixels[j] + ry};
                            }
                            branches.add(branchPix);
                            if (removeAll) {
                                for (i = length - 1; i >= 0; i--) {
                                    drawPixel(processor, xPixels[i], yPixels[i]);
                                }
                            }
                        }
                    }
                }
            }
        }
        return change;
    }

    public short[][] traceBranch(ImageProcessor processor, int x0, int y0, ArrayList<Node> nodes, int maxBranchLength) {
        int length = 0;
        short xPixels[] = new short[maxBranchLength];
        short yPixels[] = new short[maxBranchLength];

        processor.setColor(BACKGROUND);
        /*
                 * Image scanned until foreground pixel located
         */
        if (processor.getPixelValue(x0, y0) == FOREGROUND) {
            /*
                     * Tracing of branches commences from end-points only
             */
            if (SkeletonProcessor.isEndPoint(x0, y0, processor, BACKGROUND)) {
                xPixels[length] = (short) x0;
                yPixels[length] = (short) y0;
                /*
                         * Tracing of the skeleton proceeds until the end of the
                         * current branch is reached
                 */
                do {
//                    System.out.println(String.format("x: %d y: %d", xPixels[length], yPixels[length]));
                    length++;
                } while (SkeletonProcessor.getNextPixel(xPixels, yPixels, processor, length, FOREGROUND));
                for (int i = length - 1; i >= 0; i--) {
                    drawPixel(processor, xPixels[i], yPixels[i]);
                }
                int[] bp = SkeletonProcessor.getLatestBranchpoint();
                if (bp != null) {
                    drawPixel(processor, bp[0], bp[1]);
                }
            }
        }
        short[] truncXP = new short[length];
        short[] truncYP = new short[length];
        for (int i = 0; i < length; i++) {
            truncXP[i] = xPixels[i];
            truncYP[i] = yPixels[i];
        }
        return new short[][]{truncXP, truncYP};
    }

    /**
     * @return the image containing the 'pruned' skeleton structure.
     */
    public ByteProcessor getPrunedImage() {
        return outputProcessor;
    }

    public ArrayList<int[][]> getBranches() {
        return branches;
    }

    void drawPixel(ImageProcessor processor, int x, int y) {
        processor.drawPixel(x, y);
//        IJ.saveAs(new ImagePlus("", processor), "PNG", "D:\\debugging\\anamorf_debug\\step_" + index++);
    }
}
