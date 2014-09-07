package AnaMorf;

import UtilClasses.Utilities;
import IAClasses.DSPProcessor;
import IAClasses.OnlyExt;
import IAClasses.Pixel;
import UtilClasses.GenUtils;
import ij.IJ;
import ij.ImagePlus;
import ij.gui.PolygonRoi;
import ij.gui.Roi;
import ij.gui.Wand;
import ij.measure.Measurements;
import ij.measure.ResultsTable;
import ij.plugin.CanvasResizer;
import ij.plugin.PlugIn;
import ij.plugin.filter.Analyzer;
import ij.plugin.filter.BackgroundSubtracter;
import ij.plugin.filter.EDM;
import ij.plugin.filter.GaussianBlur;
import ij.plugin.filter.RankFilters;
import ij.process.Blitter;
import ij.process.ByteBlitter;
import ij.process.ByteProcessor;
import ij.process.ColorBlitter;
import ij.process.ColorProcessor;
import ij.process.FloodFiller;
import ij.process.ImageProcessor;
import ij.process.ImageStatistics;
import ij.process.TypeConverter;
import java.awt.Color;
import java.awt.Polygon;
import java.awt.Rectangle;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.PrintWriter;
import java.util.ArrayList;

/**
 * BatchAnalyser is designed to automatically analyse a batch of images of
 * filamentous microbes and ouput data on parameters such as object area,
 * circulatity, lacunarity, fractal dimension, total hyphal length, total number
 * of tips and hyphal growth unit. A detailed algorithmic description can be
 * found in the following publications:<br> <br> D. Barry, C. Chan, and G.
 * Williams, “Morphological quantification of filamentous fungal development
 * using membrane immobilization and automatic image analysis,” <i>J Ind
 * Microbiol Biot</i>, vol. 36, no. 6, pp. 787-800, 2009.<br> <br> D. J. Barry,
 * O. C. Ifeyinwa, S. R. McGee, R. Ryan, G. A. Williams, and J. M. Blackledge,
 * “Relating fractal dimension to branching behaviour in filamentous
 * microorganisms,” <i>ISAST T Elec Sig Proc</i>, vol. 1, no. 4, pp. 71–76,
 * 2009.
 *
 * @author David J Barry <davejpbarry@gmail.com>
 * @version 01SEP2010
 */
public class Batch_Analyser implements PlugIn {

    private static File currentDirectory; // The current working directory from which images are opened
    private File resultsDirectory = null; // The directory in which generated mask images are stored
    private double minCirc = 0.0, maxArea = Double.MAX_VALUE; // Morphological thresholds used during analysis
    private double imageResolution2; // Side length of one pixel in microns
    private boolean outputResults, useMorphFilters, wholeImage, noEdge = true;
    private int outputData = 0; // Determines what metrics will be output to Results Table
    private static final int FOREGROUND = 0, BACKGROUND = 255; // Values for foreground & background pixels
    private ByteProcessor maskImage, refProc;
    private ColorProcessor skelImage;
    private final ArrayList detections = new ArrayList();
    private String imageName;
    public static final String title = "AnaMorf v1.0";
    private int paramCount = 8;
    private double params[] = new double[paramCount];
    private String delimiter;
    UserInterface gui;
    /*
     * Column headings used for Results Table output
     */
    public static final String CIRC_HEAD = "Circularity",
            AREA_HEAD = "Area (" + IJ.micronSymbol + "m^2)",
            FRAC_HEAD = "Fractal Dimension",
            LAC_HEAD = "Lacunarity",
            LENGTH_HEAD = "Total Length (" + IJ.micronSymbol + "m)",
            TIP_HEAD = "Endpoints",
            HGU_HEAD = "HGU (" + IJ.micronSymbol + "m)",
            BRANCH_HEAD = "Branchpoints";

    /*
     * Flags for results output
     */
    public static final int AREAS = 1,
            CIRC = 2,
            HYPHAL_GROWTH_UNIT = 4,
            TOTAL_HYPHAL_LENGTH = 8,
            NUMBER_OF_ENDPOINTS = 16,
            NUMBER_OF_BRANCHES = 32,
            FRACTAL_DIMENSION = 64,
            LACUNARITY = 128;

    public static void main(String args[]) {
        Batch_Analyser ba = new Batch_Analyser();
        ba.run(null);
        System.exit(0);
    }

    public Batch_Analyser(boolean wholeImage) {
        this.wholeImage = wholeImage;
        noEdge = false;
    }

    public Batch_Analyser() {
    }

    /**
     * Implementation of {@link PlugIn}'s run method.
     *
     * @param arg passed by ImageJ.
     */
    public void run(String arg) {
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(UserInterface.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(UserInterface.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(UserInterface.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(UserInterface.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        if (IJ.isMacintosh()) {
            delimiter = "//";
        } else {
            delimiter = "\\";
        }
        if (!showGUI()) {
            return;
        }
        currentDirectory = Utilities.getFolder(currentDirectory, null);
        if (currentDirectory == null) {
            return;
        }
        long startTime = System.currentTimeMillis();
        if (analyseFiles(currentDirectory)) {
//            (new ResultSummariser(confInterval)).summarise();
        }
        generateParamsFile(resultsDirectory);
        IJ.showStatus(title + " done: " + ((double) (System.currentTimeMillis() - startTime)) / 1000.0 + " s");
    }

    /**
     * Carries out processing on a batch of images stored in the location
     * specified by <code>directory</code>. Each image is converted to
     * greyscale, Background- subtracted, median filtered and then thresholded
     * to produce a binary image. Each image is then subjected to a <i>close</i>
     * operation and sent to <code>analyseObjects()</code>.
     *
     * @return true if images in <i>directory</i> were successfully processed,
     * false otherwise.
     */
    public boolean analyseFiles(File directory) {
        int width, height;
        currentDirectory = directory;
        FilenameFilter directoryFilter = new OnlyExt(gui.getImageFormat());
        String imageFilenames[] = currentDirectory.list(directoryFilter); // Generates a list of image filenames of the format specified by the user
        if (imageFilenames.length < 1) {
            IJ.showMessage("'" + currentDirectory + "' contains no images of type ." + gui.getImageFormat());
            return false;
        }
        /*
         * A folder for storing mask images is created if required
         */
        resultsDirectory = new File(GenUtils.openResultsDirectory(currentDirectory.getAbsolutePath() + delimiter + title, delimiter));
        for (int i = 0; i < imageFilenames.length; i++) {
            useMorphFilters = true;
            outputResults = !wholeImage;
            imageName = imageFilenames[i];
            IJ.showStatus("Scanning " + imageName);
            ImagePlus currImage = new ImagePlus(currentDirectory + delimiter + imageName);
            width = currImage.getWidth();
            height = currImage.getHeight();
            maskImage = new ByteProcessor(width, height);
            maskImage.setColor(BACKGROUND);
            maskImage.fill();
            skelImage = new ColorProcessor(width, height);
            /*
             * Reference used to ensure that each object is only analysed once
             */
            refProc = new ByteProcessor(width, height);
            if (refProc.isInvertedLut()) {
                refProc.invertLut();
            }
            refProc.setValue(BACKGROUND);
            refProc.fill();
            if (searchImage(preProcessImage(currImage), noEdge, null, true) > 0) {
                ImagePlus maskOutput = new ImagePlus(imageName + " - Mask", maskImage.duplicate());
                maskImage.invert();
                if (gui.isCreateMasks()) {
                    IJ.saveAs(maskOutput, "png", resultsDirectory + "//" + maskOutput.getTitle());
                }
                if (wholeImage && maskOutput != null) {
                    outputResults = true;
                    useMorphFilters = false;
                    analyseImage(maskImage, maskOutput.getProcessor(), null, noEdge, null);
                }
                if (((outputData & HYPHAL_GROWTH_UNIT) != 0) || ((outputData & NUMBER_OF_ENDPOINTS) != 0)
                        || ((outputData & TOTAL_HYPHAL_LENGTH) != 0)) {
                    ImagePlus skelOutput = new ImagePlus(imageName + " - Skeleton", skelImage);
                    if (gui.isCreateMasks()) {
                        IJ.saveAs(skelOutput, "png", resultsDirectory + "//" + skelOutput.getTitle());
                    }
                }
            }
            IJ.showProgress(i, imageFilenames.length);
        }
        return true;
    }

    private ImageProcessor preProcessImage(ImagePlus currentImage) {
        ImageProcessor currentProcessor = currentImage.getProcessor();
        if (currentProcessor == null) {
            IJ.log("There was a problem reading " + currentImage.getTitle() + " in "
                    + currentDirectory.getPath());
            return null;
        }
        ByteProcessor binaryProcessor = (ByteProcessor) (new TypeConverter(currentProcessor, false)).convertToByte();
        double filterRadius = 2.0 * 1.12347 / gui.getRes();
        (new GaussianBlur()).blurGaussian(currentProcessor, filterRadius, filterRadius, 0.01);
        int iterations = (int) Math.round(filterRadius);
        int width = currentProcessor.getWidth();
        int height = currentProcessor.getHeight();

        if (!gui.isLightBackground()) {
            binaryProcessor.invert();
        }
        /*
         * Low-frequency noise removal
         */
        if (gui.isSubBackground()) {
            (new BackgroundSubtracter()).rollingBallBackground(binaryProcessor,
                    gui.getBackgroundRadius() / gui.getRes(), false, true, false,
                    false, false);
        }
        if (gui.isDoMorphFiltering()) {
            /*
             * High-frequency noise removal
             */
            RankFilters rankFilterObject = new RankFilters();
            rankFilterObject.rank(binaryProcessor, filterRadius, RankFilters.MIN);
            rankFilterObject.rank(binaryProcessor, filterRadius, RankFilters.MEDIAN);
        }
        /*
         * Generate binary image
         */
        if (gui.getManualThreshold() < 0) {
            binaryProcessor.autoThreshold();
        } else {
            binaryProcessor.threshold(gui.getManualThreshold());
        }
        if (gui.isDoMorphFiltering()) {
            /*
             * Perfom morphological 'close'
             */
            if (iterations > 0) {
                binaryProcessor.dilate(iterations, 255);
                binaryProcessor.erode(iterations, 255);
            }
            /*
             * Specify new ROI to compensate for erosion operation above
             */
            binaryProcessor.setRoi(new Rectangle(iterations, iterations,
                    (width - 2 * iterations), (height - 2 * iterations)));
            binaryProcessor = (ByteProcessor) binaryProcessor.crop();
        }
        if (gui.isDoWatershed()) {
            binaryProcessor.invert();
            (new EDM()).toWatershed(binaryProcessor);
            binaryProcessor.invert();
        }
        return binaryProcessor;
    }

    /**
     * Displays a dialog to obtain input parameters from a user.
     *
     * @return false if the dialog was closed/exited by the user, true otherwise
     */
    public boolean showGUI() {
        boolean valid = false;
        while (!valid) {
            valid = true;
            gui = new UserInterface(IJ.getInstance(), true);
            gui.setVisible(true);
            if (!gui.exitProgram()) {
                boolean options[] = gui.getOptions();
                for (int n = 0; n < paramCount; n++) {
                    if (options[n]) {
                        outputData += (int) Math.round(Math.pow(2.0, n));
                    }
                }
            } else {
                return false;
            }
        }
        imageResolution2 = Math.pow(gui.getRes(), 2.0);
        return true;
    }

    /**
     * Searches the image (represented by <code>currentImage</code>) for
     * objects, traces the outline of each object using {@link Wand}
     * <code>.autoOutline()</code> and sends each detected boundary to
     * <code>analyseImage()</code> for morphological analysis.
     *
     * @param currentImage the image to be searched
     */
    public int searchImage(ImageProcessor binaryProcessor, boolean excludeEdges, Roi roi, boolean checkGreyLevels) {
        int i, currentPixel, x, y, offset, objects;
        int width = binaryProcessor.getWidth();
        int height = binaryProcessor.getHeight();
        if (roi == null) {
            roi = new Roi(0, 0, width, height);
        }
        if (refProc == null) {
            refProc = new ByteProcessor(width, height);
            if (refProc.isInvertedLut()) {
                refProc.invertLut();
            }
            refProc.setValue(BACKGROUND);
            refProc.fill();
        }
        if (maskImage == null && gui.isCreateMasks()) {
            maskImage = new ByteProcessor(width, height);
            maskImage.setColor(BACKGROUND);
            maskImage.fill();
        }
        Rectangle bounds = roi.getBounds();
        /*
         * Ensure image is not inverted
         */
        if (checkGreyLevels) {
            binaryProcessor = ensureGreyValues(binaryProcessor, false);
        }
        byte pixels[] = (byte[]) binaryProcessor.getPixels();
        ImageProcessor filledBP = binaryProcessor.duplicate();
        fill(filledBP, 0, 255);
        Wand wand = new Wand(filledBP);
        /*
         * Image is scanned in a raster fashion searching for FOREGROUND pixels
         * that are not yet present in the reference image.
         */
        for (y = bounds.y, objects = 0; y < bounds.height; y++) {
            offset = y * width;
            for (x = bounds.x; x < bounds.width; x++) {
                i = offset + x;
                currentPixel = pixels[i] & 0xff;
                if (roi.contains(x, y) && currentPixel == FOREGROUND
                        && refProc.getPixelValue(x, y) == BACKGROUND) {
                    /*
                     * Ensure that a foreground pixel exists 'south-east' of the
                     * current position
                     */
                    IJ.showStatus("Generating Outline");
                    wand.autoOutline(x, y, 0.0, Wand.EIGHT_CONNECTED);
                    if (analyseObject(wand.xpoints, wand.ypoints, wand.npoints,
                            binaryProcessor.duplicate(), excludeEdges, roi)) {
                        objects++;
                    }
                }
            }
        }
        return objects;
    }

    /**
     * Ensures that an image has a non-inverted look-up table (LUT) and is
     * either inverted or non-inverted
     *
     * @param processor the image to be processed
     * @param inversion true if image is to be inverted, false otherwise
     * @return inverted/non-inverted binary image with standard LUT
     */
    public ImageProcessor ensureGreyValues(ImageProcessor processor, boolean inversion) {
        if (processor.isInvertedLut()) {
            processor.invertLut();
        }
        /*
         * Ensure image is binary
         */
        if (!processor.isBinary()) {
            processor.autoThreshold();
        }
        ImageStatistics stats = ((new TypeConverter(processor, true)).convertToByte()).getStatistics();
        /*
         * If there are more FOREGROUND pixels (black) than BACKGROUND pixels
         * (white) but the image is not supposed to be inverted (<i>inversion ==
         * false</i>), or there are more BACKGROUND pixels than FOREGROUND
         * pixels and the image should be inverted, then the processor is
         * inverted before returning.
         */
        if ((stats.histogram[FOREGROUND] > stats.histogram[BACKGROUND]) != inversion) {
            processor.invert();
        }
        return processor;
    }

    /**
     * The morphology of an object, the boundary of which is specified by the
     * points stored in <i>xPoints</i> and <i>yPoints</i>, within the image
     * represented by <i>image</i>, is analysed, subject to the thresholds
     * specified in <i>initialise(String, double, double, double, double,
     * double)</i>.
     *
     * @param xPoints the x-coordinates of the object boundary
     * @param yPoints the y-coordinates of the object boundary
     * @param points the total number of points representing the object boundary
     * @param count index used to differentiate between objects in the same
     * image
     * @param image represents the image in which the object is located
     * @param refProc reference image, into which objects are copied after
     * detection, to ensure no object is analysed more than once
     */
    public boolean analyseObject(int[] xPoints, int[] yPoints, int points,
            ImageProcessor binaryProc, boolean excludeEdges, Roi imageRoi) {
        /*
         * A polygon representing the object border is constructed, the mask of
         * which is copied into the reference image.
         */
        PolygonRoi objectRoi = getPolygonRoi(points, xPoints, yPoints);
        ByteProcessor objectMask = (ByteProcessor) objectRoi.getMask();
        return analyseImage(objectMask, binaryProc, objectRoi, excludeEdges,
                imageRoi);
    }

    public boolean analyseImage(ByteProcessor objMask,
            ImageProcessor binProc, PolygonRoi objRoi, boolean excludeEdges, Roi imageRoi) {
        int x, y, pixArea = 0, numEnds = 0, numBranches = 0;
        int minPixLength = (int) Math.round(gui.getMinLength() / gui.getRes());
        double var, meanSq, objArea, objCirc, xCent, yCent;
        double objectPerim = 1.0, lac = 1.0, distfracDim = Double.NaN;
        double xSum = 0.0, ySum = 0.0, growthUnit = 0.0, totalLength = 0.0;
        Rectangle objBox, imageBox = new Rectangle(0, 0, binProc.getWidth(), binProc.getHeight());
        if (objRoi != null) {
            Polygon poly = objRoi.getPolygon();
            objBox = poly.getBounds();
        } else {
            objBox = new Rectangle(0, 0, objMask.getWidth(), objMask.getHeight());
        }
        IJ.showStatus("Calculating Area");
        if (refProc != null) {
            refProc.setColor(FOREGROUND);
        }
        for (y = objBox.y; y <= (objBox.height + objBox.y); y++) {
            for (x = objBox.x; x <= (objBox.width + objBox.x); x++) {
                if (objMask.getPixel(x - objBox.x, y - objBox.y) == BACKGROUND) {
                    if ((binProc.getPixelValue(x, y) == FOREGROUND)
                            && (refProc.getPixel(x, y) == BACKGROUND)) {
                        xSum += x - objBox.x;
                        ySum += y - objBox.y;
                        pixArea++;
                        if (refProc != null) {
                            refProc.drawPixel(x, y);
                        }
                    } /*
                     * else { objMask.putPixel(x - objBox.x, y - objBox.y,
                     * FOREGROUND); }
                     */

                }
            }
        }
        Rectangle imageRoiBounds = (imageRoi == null)
                ? new Rectangle(0, 0, binProc.getWidth(), binProc.getHeight()) : imageRoi.getBounds();
        if (excludeEdges && Utilities.checkBounds(objBox, imageRoiBounds)) {
            return false;
        }
        objArea = pixArea * imageResolution2;
        try {
            if (objRoi != null) {
                objectPerim = objRoi.getLength();
            }
        } catch (ArrayIndexOutOfBoundsException e) {
            return false;
        }

        /*
         * A perfect circle has circularity = 1. As perimeter increases relative
         * to area, circularity tends to zero.
         */
        objCirc = (4 * Math.PI * pixArea) / (objectPerim * objectPerim);
        if (objCirc > 1.0) {
            objCirc = 1.0;
        }
        if (useMorphFilters) {
            /*
             * Check area and circularity against user-specified threshold
             * values.
             */
            if ((objArea < gui.getMinArea()) || (objArea > maxArea)
                    || (objCirc < minCirc) || (objCirc > gui.getMaxCirc())) {
                return false;
            }
            binProc.setRoi(objBox);
            binProc = binProc.crop(); // Reduce image size to reduce processing time
            objMask.invert();

            /*
             * Remove any FOREGROUND pixels that do not belong to this object
             */
            ByteBlitter objBlit = new ByteBlitter((ByteProcessor) binProc);
            objBlit.copyBits(objMask, 0, 0, Blitter.MAX);

            ByteBlitter maskBlit = new ByteBlitter(maskImage);
            maskBlit.copyBits(binProc, objBox.x, objBox.y, Blitter.MIN);
            objMask.invert();
            detections.add(objMask.duplicate());
        }

        if (((outputData & HYPHAL_GROWTH_UNIT) != 0) || ((outputData & NUMBER_OF_ENDPOINTS) != 0)
                || ((outputData & TOTAL_HYPHAL_LENGTH) != 0) || ((outputData & LACUNARITY) != 0)) {
            /*
             * An estimate of the object's lacunarity is used to determine
             * whether an accurate evaluation of hyphal length and number of
             * hyphal tips is possible.
             */
            IJ.showStatus("Calculating Lacunarity");
            binProc.setMask(objMask); // Restrict calculation of lacunarity to within object boundary
            binProc.invert(); // Foreground pixels = 255, to ensure non-zero mean
            ImageStatistics objStats = ImageStatistics.getStatistics(binProc,
                    Measurements.MEAN + Measurements.STD_DEV, null);
            var = Math.pow(objStats.stdDev, 2);
            meanSq = Math.pow(objStats.mean, 2);
            lac = Math.abs((var / meanSq) - 1.0);
            if (((outputData & HYPHAL_GROWTH_UNIT) != 0) || ((outputData & NUMBER_OF_ENDPOINTS) != 0)
                    || ((outputData & TOTAL_HYPHAL_LENGTH) != 0)) {
                IJ.showStatus("Calculating HGU");
                binProc.invert(); // Reverse inversion above
                CanvasResizer resizer = new CanvasResizer();
                /*
                 * Draw a white border around object to ensure no 'contact'
                 * between object and image boundary
                 */
                ByteProcessor objProc = (ByteProcessor) resizer.expandImage(binProc,
                        (objBox.width + 2), (objBox.height + 2), 1, 1);
                /*
                 * Skeletonise image for determinations of hyphal length and tip
                 * number
                 */
                objProc.skeletonize();
                /*
                 * Prune image to remove artefacts of skeletonisation
                 */
                SkeletonPruner pruner = new SkeletonPruner(minPixLength, objProc);
                objProc = pruner.getPrunedImage();
                HyphalAnalyser analyser = new HyphalAnalyser(objProc, gui.getRes(), imageBox, objBox);
                analyser.analyse(); // Analyse pruned skeleton
                growthUnit = analyser.getHGU();
                totalLength = analyser.getLength();
                numEnds = analyser.getTips();
                numBranches = analyser.getBranchpoints();
                ColorBlitter skelBlit = new ColorBlitter(skelImage);
                skelBlit.copyBits(analyser.getOutput(), 0, 0, Blitter.ADD);
            }
        }

        if ((outputData & FRACTAL_DIMENSION) != 0) {
            IJ.showStatus("Calculating Boundary Fractal");
            /*
             * A different form of <i>PolygonRoi</i> (<i>Roi.POLYGON</i>) is
             * contructed to provide an accurate determination of the number of
             * points on the object boundary
             */
            PolygonRoi polyObjRoi = new PolygonRoi(objRoi.getXCoordinates(),
                    objRoi.getYCoordinates(), objRoi.getNCoordinates(), Roi.POLYGON);
            ByteProcessor polyproc = new ByteProcessor(binProc.getWidth(), binProc.getHeight());
            polyproc.setColor(Color.white);
            polyproc.draw(polyObjRoi);
            xCent = xSum / pixArea;
            yCent = ySum / pixArea;
            Pixel boundPoints[] = DSPProcessor.getDistanceSignal(polyObjRoi.getNCoordinates(),
                    xCent, yCent, polyObjRoi.getXCoordinates(),
                    polyObjRoi.getYCoordinates(), gui.getRes());
            double dist[] = new double[boundPoints.length];
            for (int i = 0; i < boundPoints.length; i++) {
                dist[i] = boundPoints[i].getZ();
            }
            double[] upscaledDistInput = DSPProcessor.upScale(dist);
            double sampleRate = (1.0d / gui.getRes())
                    * upscaledDistInput.length / dist.length;
            double distfracparams[] = DSPProcessor.calcFourierDim(DSPProcessor.calcFourierSpec(upscaledDistInput, sampleRate), sampleRate, 1.0);
            if (distfracparams != null) {
                distfracDim = (5.0 - Math.abs(distfracparams[1])) / 2.0;
            } else {
                distfracDim = Double.NaN;
            }
            params[4] = dist.length;
        }

        params[0] = objCirc;
        params[1] = objArea;
        params[2] = distfracDim;
        params[3] = lac;

        if (outputResults && (outputData != 0)) {
            /*
             * Output results to ImageJ's results table.
             */
            Analyzer analyserObject = new Analyzer();
            ResultsTable resultsTable = Analyzer.getResultsTable();
            resultsTable.incrementCounter();
            if ((outputData & CIRC) != 0) {
                resultsTable.addValue(CIRC_HEAD, objCirc);
            }
            if ((outputData & AREAS) != 0) {
                resultsTable.addValue(AREA_HEAD, objArea);
            }
            if ((outputData & FRACTAL_DIMENSION) != 0) {
                resultsTable.addValue(FRAC_HEAD, distfracDim);
            }
            if ((outputData & LACUNARITY) != 0) {
                resultsTable.addValue(LAC_HEAD, lac);
            }
            if ((outputData & TOTAL_HYPHAL_LENGTH) != 0) {
                resultsTable.addValue(LENGTH_HEAD, (totalLength * gui.getRes()));
            }
            if ((outputData & NUMBER_OF_ENDPOINTS) != 0) {
                resultsTable.addValue(TIP_HEAD, numEnds);
            }
            if ((outputData & HYPHAL_GROWTH_UNIT) != 0) {
                resultsTable.addValue(HGU_HEAD, (growthUnit * gui.getRes()));
            }
            if ((outputData & NUMBER_OF_BRANCHES) != 0) {
                resultsTable.addValue(BRANCH_HEAD, numBranches);
            }
            resultsTable.addLabel("Image", imageName);
            analyserObject.displayResults();
        }

        return true;
    }

    /**
     * Constructs a PolygonRoi object from a set of input coordinates.
     *
     * @param nPoints the total number of points
     * @param xPoints the x-coordinates
     * @param yPoints the y-coordinates
     * @return the constructed PolygonRoi
     */
    public PolygonRoi getPolygonRoi(int nPoints, int[] xPoints, int[] yPoints) {
        if ((xPoints.length == nPoints) && (yPoints.length == nPoints)) {
            return new PolygonRoi(xPoints, yPoints, nPoints, Roi.TRACED_ROI);
        }

        int xPolyPoints[] = new int[nPoints];
        int yPolyPoints[] = new int[nPoints];

        /*
         * This for loop ensures the boundary coordinates are contained in
         * arrays of size <i>nPoints</i>.
         */
        for (int j = 0; j < nPoints; j++) {
            xPolyPoints[j] = xPoints[j];
            yPolyPoints[j] = yPoints[j];
        }

        /*
         * A polygon representing the object border is constructed
         */
        return new PolygonRoi(xPolyPoints, yPolyPoints, nPoints, Roi.TRACED_ROI);
    }

    /**
     * Initialise parameters for analysis.
     *
     * @param format the format of images to be analysed in the working
     * directory
     * @param length the minimum branch length (in microns) to be considered for
     * analysis
     * @param minArea the minimum area (in microns squared) needed for an object
     * to be included in the results
     * @param maxCirc the maximum circularity permissable for an object to be
     * included in the results
     * @param lacTol the total length and number of tips of an object will not
     * be evaluated if |1.0 - lacunarity| > <i>lacTol</i>.
     * @param resolution the image resolution (in microns per pixel).
     * @param confidence the confidence interval used in statistical analysis of
     * the results.
     */
    public void initialise(double maxArea, double minCirc, double resolution) {
        this.maxArea = maxArea;
        this.minCirc = minCirc;
        imageResolution2 = Math.pow(gui.getRes(), 2.0);
        useMorphFilters = true;
    }

    public void setOutputData(int outputData) {
        this.outputData = outputData;
    }

    public ByteProcessor getMaskImage() {
        return maskImage;
    }

    public ArrayList getDetections() {
        return detections;
    }

    public double[] getParams() {
        return params;
    }

    void fill(ImageProcessor ip, int foreground, int background) {
        int width = ip.getWidth();
        int height = ip.getHeight();
        FloodFiller ff = new FloodFiller(ip);
        ip.setColor(127);
        for (int y = 0; y < height; y++) {
            if (ip.getPixel(0, y) == background) {
                ff.fill(0, y);
            }
            if (ip.getPixel(width - 1, y) == background) {
                ff.fill(width - 1, y);
            }
        }
        for (int x = 0; x < width; x++) {
            if (ip.getPixel(x, 0) == background) {
                ff.fill(x, 0);
            }
            if (ip.getPixel(x, height - 1) == background) {
                ff.fill(x, height - 1);
            }
        }
        byte[] pixels = (byte[]) ip.getPixels();
        int n = width * height;
        for (int i = 0; i < n; i++) {
            if (pixels[i] == 127) {
                pixels[i] = (byte) background;
            } else {
                pixels[i] = (byte) foreground;
            }
        }
    }

    void generateParamsFile(File dir) {
        File params;
        PrintWriter outputStream;
        try {
            params = new File(dir + delimiter + "parameters.txt");
        } catch (Exception e) {
            IJ.error(e.toString());
            return;
        }
        try {
            outputStream = new PrintWriter(new FileOutputStream(params));
        } catch (FileNotFoundException e) {
            IJ.error("Could not write to results file.");
            return;
        }
        outputStream.println(title);
        outputStream.println();
        outputStream.println(gui.toString());
        outputStream.close();
    }
}
