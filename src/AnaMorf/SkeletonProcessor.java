package AnaMorf;

import ij.process.ImageProcessor;

/**
 * A collection of static utility methods for analysing and/or editing binary
 * skeleton structures.
 * @author   David J Barry <davejpbarry@gmail.com>
 * @version  05OCT2010
 */
public class SkeletonProcessor {
    /**
     * A negative value used as a flag to indicate a branch-point has been found.
     */
    public final static int BRANCH = -1;

    /**
     * Determines whether (<i>xLoc</i>, <i>yLoc</i>) is an 'end-point' on a skeleton.
     *
     * @param xLoc x-coordinate in image
     * @param yLoc y-coordinate in image
     * @param processor image containing skeleton structure
     * @return true if (<i>xLoc</i>, <i>yLoc</i>) is an 'end-point', false otherwise.
     */
    public static boolean isEndPoint(int xLoc, int yLoc, ImageProcessor processor, int background) {
        int i, bgPixels, maxCount = 0;
        int neighbourhood[] = new int[8];

        neighbourhood[0] = processor.getPixel(xLoc - 1, yLoc - 1);
        neighbourhood[1] = processor.getPixel(xLoc , yLoc - 1);
        neighbourhood[2] = processor.getPixel(xLoc + 1, yLoc - 1);
        neighbourhood[3] = processor.getPixel(xLoc + 1, yLoc);
        neighbourhood[4] = processor.getPixel(xLoc + 1, yLoc + 1);
        neighbourhood[5] = processor.getPixel(xLoc, yLoc + 1);
        neighbourhood[6] = processor.getPixel(xLoc - 1, yLoc + 1);
        neighbourhood[7] = processor.getPixel(xLoc - 1, yLoc);
        /*
         * The following loop determines the length of the longest 'chain' of
         * background pixels surrounding (xLoc,yLoc).
         */
        for(i = 0, bgPixels = 0; i < 16; i++){
            if(neighbourhood[i%8] == background) bgPixels++;
            else{
                if(bgPixels > maxCount) maxCount = bgPixels;
                bgPixels = 0;
            }
        }
        if(maxCount > 5) return true;
        else return false;
    }

    /**
         * Determines the existance of 'branch-points' within a skeletal structure.
         * @param xLoc the current x coordinate, the immediate neighbourhood of which
         * (radius = 1) is searched for a branch-point.
         * @param yLoc the current y coordinate
         * @param processor the image being analysed
         * @return 0 if (<i>xLoc</i>, <i>yLoc</i>) represents a branch-point, non-zero otherwise
         */
    public static int isBranchPoint(int xLoc, int yLoc, ImageProcessor processor, int foreground){
        int x, y, previous, width = processor.getWidth(),
            height = processor.getHeight();

            /*
             * This loop searches for adjacent black pixels to the 'north' and
             * 'south' of (xLoc, yLoc).
             */
            for(y = yLoc - 1; y <= yLoc + 1; y += 2){
                previous = -1;
                for(x = xLoc - 1; x <= xLoc + 1; x++){
                    if (x < 0 || x >= width || y < 0 || y >= height) break;
                    if((processor.getPixelValue(x,y) == foreground) &&
                        (previous == foreground)) return ((y - yLoc) * width);
                    previous = processor.getPixel(x,y);
                }
            }
            /*
             * This loop searches for adjacent black pixels to the 'east' and
             * 'west' of (xLoc, yLoc).
             */
            for(x = xLoc - 1; x <= xLoc + 1; x += 2){
                previous = -1;
                for(y = yLoc - 1; y <= yLoc + 1; y++){
                    if (x < 0 || x >= width || y < 0 || y >= height) break;
                    if((processor.getPixelValue(x,y) == foreground) &&
                        (previous == foreground)) return (x - xLoc);
                    previous = processor.getPixel(x,y);
                }
            }
        return 0;
    }

    /**
     * Searches the 3 x 3 neighbourhood of position <i>(x,y)</i> in <i>matrix</i>
     * for <i>key</i>.
     * @param x x-coordinate of centre of neighbourhood to be searched
     * @param y y-coordinate
     * @param w width of <i>matrix</i> or width of search area
     * @param h height of <i>matrix</i> or height of search area
     * @param key value to be located
     * @param matrix 2-D array to be searched
     * @return true if <i>key</i> is located, false otherwise
     */
    public static boolean isConnected(int x, int y, int w, int h, int key, int matrix[][]){
        int i, j;

        for(i = (x > 0) ? x - 1 : 0; (i <= x + 1) && (i < w); i++){
            for(j = (y > 0) ? y - 1: 0; (j <= y + 1) && (j < h); j++){
                if(!((i == x) && (j == y)))
                    if(matrix[i][j] == key) return true;
            }
        }
        return false;
    }

    /**
     * Determines whether a foreground pixel, specified by <i>(xPix,yPix)</i>,
     * can be removed from the image contained in <i>processor</i> without
     * creating a break in the skeleton object contained therein.
     * @param xPix x coordinate of the pixel to be tested
     * @param yPix y coordinate
     * @param processor image containing a skeleton object
     * @return true if pixel can be safely removed, false otherwise
     */
    public static  boolean removePixel(int xPix, int yPix, ImageProcessor processor, int foreground, int background) {
        int x, y, fgPix = 0, connectedPix = 0, radius = 1, width = 2 * radius + 1;
        int neighbourhood[][] = new int[width][width];

        /*
         * A matrix is constructed representing the 3 x 3 neighbourhood of
         * (xPix, yPix).
         */
        for(x = -radius; x <= radius; x++){
            for(y = -radius; y <= radius; y++){
                if(x == 0 && y == 0)
                    neighbourhood[x+radius][y+radius] = background;
                else neighbourhood[x+radius][y+radius]
                        = processor.getPixel(x+xPix,y+yPix);
            }
        }
        /*
         * The following loop tests whether each foreground pixel in the 3 x 3
         * neighbourhood is 8-connected to another foreground pixel.
         */
        for(x = 0; x < width; x++){
            for(y = 0; y < width; y++){
                if(neighbourhood[x][y] == foreground){
                    fgPix++;
                    if(isConnected(x, y, width, width, foreground, neighbourhood))
                        connectedPix++;
                }
            }
        }
        if(fgPix != connectedPix) return false;
        else return true;
    }

    /**
     * Searches up to <i>length</i> entries in <i>array1</i> and <i>array2</i>
     * for <i>key1</i> and <i>key2</i> respectively.
     * @param array1 array of integers
     * @param array2 array of integers
     * @param length cut-off index for search
     * @param key1 integer to be located in <i>array1</i>
     * @param key2 integer to be located in <i>array2</i>
     * @return <i>length - index</i> if both <i>key1</i> and <i>key2</i> were
     * located at the same array <i>index</i>, -1 otherwise.
     */
    public static int pathSearch(int array1[], int array2[], int length, int key1,
            int key2){
        int i;

        for(i = 0; i < length; i++){
            if(array1[i] == key1 && array2[i] == key2) return length - i;
        }
        return -1;
    }

    /**
     * Searches for the next foreground pixel adjacent to a specified coordinate.
     * It is assumed that this is part of a process of searching for a path within
     * a skeleton.
     *
     * @param xpix the x coordinates of the path obtained to date
     * @param ypix the y coordiantes
     * @param processor the image containing the skeleton object
     * @param length the length of the path stored in <i>xpix</i> and <i>ypix</i>
     * @return true if a foreground pixel has been located adjacent to
     * (<i>xpix[length-1]</i>, <i>ypix[length-1]</i>), false if
     * (<i>xpix[length-1]</i>, <i>ypix[length-1]</i>) is a branch-point or if
     * (<i>xpix[length]</i>, <i>ypix[length]</i>) has already been located (a
     * fail-safe to ensure the loop does not 'double-back' on itself).
     */
    public static boolean getNextPixel(int xpix[], int ypix[], ImageProcessor processor,
            int length, int foreground){
        int x, y, move, fgPix = 0, search;
        int width = processor.getWidth(), height=processor.getHeight();
        int current = length - 1;
        int last = (current > 0) ? current - 1 : current;
        int next = current + 1;

        /*
         * The following loop searces for a foreground pixel adjacent to
         * (xpix[current],ypix[current]), with checks to ensure the current or
         * previous (last) pixels are omitted from the search.
         */
        for(y = (ypix[current] > 0) ? ypix[current] - 1: 0; (y <= ypix[current] + 1)
                && y < height; y++) {
            for(x = (xpix[current] > 0) ? xpix[current] - 1: 0; (x <= xpix[current] + 1)
                    && x < width; x++) {
                if ((processor.getPixelValue(x,y) == foreground) &&
                        !((y == ypix[last]) && (x == xpix[last])) &&
                        !((x == xpix[current]) && (y == ypix[current]))) {
                    xpix[next] = x;
                    ypix[next] = y;
                    fgPix++;
                }
            }
        }
        if(fgPix < 1) return false; // End of branch reached
        if(fgPix > 1){
            /*
             * If more than one foreground pixel has been located, a check is
             * performed to determine if (xpix[current],ypix[current]) is a
             * branch-point.
             */
            move = isBranchPoint(xpix[current], ypix[current],
                    processor, foreground);
            if(move != 0){
                if(Math.abs(move) > 1){
                    ypix[next] = ypix[current] + (move/width);
                    xpix[next] = xpix[current];
                } else {
                    xpix[next] = xpix[current] + move;
                    ypix[next] = ypix[current];
                }
                /*
                 * This is a fail-safe to ensure that the loop does not double-
                 * back on itself and cause an infinite loop.
                 */
                search = pathSearch(xpix, ypix, next, xpix[next], ypix[next]);
                if(search > 0){
                    xpix[next] = ypix[next] = -search;
                    return false;
                }
            } else{
                xpix[next] = ypix[next] = BRANCH;
                return false;
            }
        }
        return true;
    }
}
