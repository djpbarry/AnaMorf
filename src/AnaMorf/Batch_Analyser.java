package AnaMorf;

import IAClasses.DSPProcessor;
import IAClasses.OnlyExt;
import IAClasses.Pixel;
import ij.IJ;
import ij.ImagePlus;
import ij.gui.GenericDialog;
import ij.gui.PolygonRoi;
import ij.gui.Roi;
import ij.gui.Wand;
import ij.measure.Measurements;
import ij.measure.ResultsTable;
import ij.plugin.CanvasResizer;
import ij.plugin.PlugIn;
import ij.plugin.filter.Analyzer;
import ij.plugin.filter.BackgroundSubtracter;
import ij.plugin.filter.RankFilters;
import ij.process.*;
import java.awt.Color;
import java.awt.Polygon;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.io.File;
import java.io.FilenameFilter;
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

    private static File currentDirectory = new File("C:\\Users\\barry05\\Desktop\\TestAnaMorf"); // The current working directory from which images are opened
    private File maskImageDirectory = null; // The directory in which generated mask images are stored
    private double minBranchLength = 40.0, maxCirc = 0.05, minCirc = 0.0, minArea = 1000.0, maxArea = Double.MAX_VALUE, lacTol = 100.0; // Morphological thresholds used during analysis
    private double confInterval = 95.0; // Confidence Interval for statistical anaylis of results
    private double imageRes = 1.12347, imageResolution2 = imageRes * imageRes; // Side length of one pixel in microns
    private boolean createMaskImages = true; // Folder of mask images created if set to true
    private boolean subtractBackground = true; // Background subtraction performed on each image if set to true
    private boolean lightBackground = false; // If set to true, images assumed to be dark objects on light backgrounds
    private boolean outputResults, useMorphFilters, wholeImage, noEdge;
    private int outputData = 0; // Determines what metrics will be output to Results Table
    private int manualThreshold = -1; // Grey level threshold - if < 0, autoThreshold() used
    private static final int FOREGROUND = 0, BACKGROUND = 255; // Values for foreground & background pixels
    private String imageFormat = "TIF"; // Only images of this format are processed
    private ByteProcessor maskImage, refProc;
    private ColorProcessor skelImage;
    private ArrayList detections = new ArrayList();
    private String imageName;
    public static final String title = "AnaMorf v1.0";
    private double params[] = new double[5];

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
            FRACTAL_DIMENSION = 4,
            LACUNARITY = 8,
            TOTAL_HYPHAL_LENGTH = 16,
            NUMBER_OF_ENDPOINTS = 32,
            HYPHAL_GROWTH_UNIT = 64,
            NUMBER_OF_BRANCHES = 128;

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
        if (!showGUI()) {
            return;
        }
        currentDirectory = Utilities.getFolder(currentDirectory, null);
        if (currentDirectory == null) {
            return;
        }
        if (analyseFiles(currentDirectory)) {
//            (new ResultSummariser(confInterval)).summarise();
        }
        IJ.showStatus(title + ": done.");
    }

    /**
     * Carries out processing on a batch of images stored in the location
     * specified by
     * <code>directory</code>. Each image is converted to greyscale, Background-
     * subtracted, median filtered and then thresholded to produce a binary
     * image. Each image is then subjected to a <i>close</i> operation and sent
     * to
     * <code>analyseObjects()</code>.
     *
     * @return true if images in <i>directory</i> were successfully processed,
     * false otherwise.
     */
    public boolean analyseFiles(File directory) {
        int width, height;
        currentDirectory = directory;
        FilenameFilter directoryFilter = new OnlyExt(imageFormat);
        String imageFilenames[] = currentDirectory.list(directoryFilter); // Generates a list of image filenames of the format specified by the user
        if (imageFilenames.length < 1) {
            IJ.showMessage("'" + currentDirectory + "' contains no images of type ." + imageFormat);
            return false;
        }
        /*
         * A folder for storing mask images is created if required
         */
        if (createMaskImages) {
            try {
                if (IJ.isMacintosh()) {
                    maskImageDirectory = new File(currentDirectory + "//Ana Morf Mask Images");
                } else {
                    maskImageDirectory = new File(currentDirectory + "\\Ana Morf Mask Images");
                }
                if (!maskImageDirectory.exists()) {
                    if (!maskImageDirectory.mkdir()) {
                        IJ.error("Failed to create mask image directory.");
                    }
                }
            } catch (Exception e) {
                IJ.error(e.toString());
            }
        }
        for (int i = 0; i < imageFilenames.length; i++) {
            useMorphFilters = true;
            outputResults = !wholeImage;
            imageName = imageFilenames[i];
            IJ.showStatus("Scanning " + imageName);
            ImagePlus currImage = new ImagePlus(currentDirectory + "\\" + imageName);
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
            searchImage(preProcessImage(currImage), noEdge, null, true);
            ImagePlus maskOutput = new ImagePlus(imageName + " - Mask", maskImage.duplicate());
            maskImage.invert();
            if (createMaskImages) {
                IJ.saveAs(maskOutput, "png", maskImageDirectory + "//" + maskOutput.getTitle());
            }
            if (wholeImage && maskOutput != null) {
                outputResults = true;
                useMorphFilters = false;
                analyseImage(maskImage, maskOutput.getProcessor(), null, noEdge, null);
            }
            ImagePlus skelOutput = new ImagePlus(imageName + " - Skeleton", skelImage);
            if (createMaskImages) {
                IJ.saveAs(skelOutput, "png", maskImageDirectory + "//" + skelOutput.getTitle());
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
        double filterRadius = 2.0 * 1.12347 / imageRes;
        ByteProcessor binaryProcessor;
        BackgroundSubtracter backgroundSubtractor = new BackgroundSubtracter();
        int iterations = (int) Math.round(filterRadius);
        int width = currentProcessor.getWidth();
        int height = currentProcessor.getHeight();
        /*
         * Colour images are seperated into their constituent channels and that
         * with the highest contrast is retained for analysis. All other
         * processor types are simply converted to ByteProcessor.
         */
        if (currentProcessor instanceof ColorProcessor) {
            binaryProcessor = Utilities.getHighContrastGreyImage((ColorProcessor) currentProcessor);
        } else {
            TypeConverter converter = new TypeConverter(currentProcessor, false);
            binaryProcessor = (ByteProcessor) converter.convertToByte();
        }
        if (!lightBackground) {
            binaryProcessor.invert();
        }
        /*
         * Low-frequency noise removal
         */
        if (subtractBackground) {
            backgroundSubtractor.rollingBallBackground(binaryProcessor,
                    50.0d / imageRes, false, true, false, false, false);
        }
        /*
         * High-frequency noise removal
         */
        RankFilters rankFilterObject = new RankFilters();
        rankFilterObject.rank(binaryProcessor, filterRadius, RankFilters.MIN);
        rankFilterObject.rank(binaryProcessor, filterRadius, RankFilters.MEDIAN);
        /*
         * Generate binary image
         */
        if (manualThreshold < 0) {
            binaryProcessor.autoThreshold();
        } else {
            binaryProcessor.threshold(manualThreshold);
        }
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
        return binaryProcessor.crop();
    }

    /**
     * Displays a dialog to obtain input parameters from a user.
     *
     * @return false if the dialog was closed/exited by the user, true otherwise
     */
    public boolean getInputParameters() {
        boolean valid = false;
        while (!valid) {
            valid = true;
            GenericDialog gui = new GenericDialog(title);
            String[] imageFormats = {"BMP", "GIF", "JPG", "PNG", "TIF"};
            String[] checkLabels = {"Create Mask Images?", "Subtract Background?", "Light Background?"};
            String[] optionLabels = {"Area", "Total Length", "Number of Endpoints", "Number of Branchpoints"};
            boolean[] optionChoices = {true, true, true, true};
            boolean[] checkValues = {createMaskImages, subtractBackground, lightBackground};
            gui.addChoice("Image Format:", imageFormats, imageFormat);
            gui.addNumericField("Image Resolution:", imageRes, 3, 5, IJ.micronSymbol + "m/pixel");
            gui.addNumericField("Minimum Branch Length:", minBranchLength, 1, 5, IJ.micronSymbol + "m");
            gui.addNumericField("Maximum Circularity:", maxCirc, 3, 5, "");
            gui.addNumericField("Minimum Area:", minArea, 3, 5, IJ.micronSymbol + "m^2");
            gui.addCheckboxGroup(2, 2, checkLabels, checkValues);
            gui.addCheckboxGroup(2, 2, optionLabels, optionChoices);
            gui.showDialog();
            if (!gui.wasCanceled()) {
                imageFormat = gui.getNextChoice();
                imageRes = gui.getNextNumber();
                minBranchLength = gui.getNextNumber();
                maxCirc = gui.getNextNumber();
                minArea = gui.getNextNumber();
                createMaskImages = gui.getNextBoolean();
                subtractBackground = gui.getNextBoolean();
                lightBackground = gui.getNextBoolean();

                if (gui.getNextBoolean()) {
                    outputData += AREAS;
                }
                if (gui.getNextBoolean()) {
                    outputData += TOTAL_HYPHAL_LENGTH;
                }
                if (gui.getNextBoolean()) {
                    outputData += NUMBER_OF_ENDPOINTS;
                }
                if (gui.getNextBoolean()) {
                    outputData += NUMBER_OF_BRANCHES;
                }
                if (gui.invalidNumber()) {
                    Toolkit.getDefaultToolkit().beep();
                    IJ.error("Entries must be numeric!");
                    valid = false;
                }
            } else {
                return false;
            }
        }
        imageResolution2 = imageRes * imageRes;
        return true;
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
            UserInterface gui = new UserInterface(IJ.getInstance(), true);
            gui.setVisible(true);
            if (!gui.exitProgram()) {
                imageFormat = gui.getImageFormat();
                imageRes = gui.getRes();
                minBranchLength = gui.getMinLength();
                maxCirc = gui.getMaxCirc();
                minArea = gui.getMinArea();
                createMaskImages = gui.isCreateMasks();
                subtractBackground = gui.isSubBackground();
                lightBackground = gui.isLightBackground();

                if (gui.isArea()) {
                    outputData += AREAS;
                }
                if (gui.isTHL()) {
                    outputData += TOTAL_HYPHAL_LENGTH;
                }
                if (gui.isTips()) {
                    outputData += NUMBER_OF_ENDPOINTS;
                }
                if (gui.isBranches()) {
                    outputData += NUMBER_OF_BRANCHES;
                }
            } else {
                return false;
            }
        }
        imageResolution2 = imageRes * imageRes;
        return true;
    }

    /**
     * Searches the image (represented by
     * <code>currentImage</code>) for objects, traces the outline of each object
     * using {@link Wand}
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
        if (maskImage == null && createMaskImages) {
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
                    //n = neighbourhood(x, y, binaryProcessor);
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
     * Ensures compatability with <i>Wand.autoOutline()</i> - checks whether the
     * pixel 'south-east' of the current pixel is FOREGROUND.
     *
     * @param x x position in image
     * @param y y position in image
     * @param processor the image being analysed
     * @return 1 if (x + 1, y + 1) is a FOREGROUND pixel, 0 otherwise
     */
    public int neighbourhood(int x, int y, ImageProcessor processor) {
        if (processor.getPixelValue(x + 1, y + 1) == FOREGROUND) {
            return 1;
        } else {
            return 0;
        }
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
        int x, y, pixArea = 0, numEnds = 0, numBranches = 0, perimPoints;
        int minPixLength = (int) Math.round(minBranchLength / imageRes);
        double var, meanSq, objArea, objCirc, xCent, yCent;
        double objectPerim = 1.0, lac = 1.0, distfracDim = Double.NaN;;//, smoothedfracDim = Double.NaN, distfracDimFit = Double.NaN, smoothedfracDimFit = Double.NaN, truncdistfracDim = Double.NaN, truncdistfracDimFit = Double.NaN;
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
        if (useMorphFilters) {
            /*
             * Check area and circularity against user-specified threshold
             * values.
             */
            if ((objArea < minArea) || (objArea > maxArea)
                    || (objCirc < minCirc) || (objCirc > maxCirc)) {
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
//            if (pixArea > 1000) {
//                new ImagePlus("BinProc",binProc).show();
//                new ImagePlus("Mask",binProc).show();
//                IJ.wait(0);
//            }
            var = Math.pow(objStats.stdDev, 2);
            meanSq = Math.pow(objStats.mean, 2);
            lac = Math.abs((var / meanSq) - 1.0);
            if (Math.abs(1.0 - lac) < lacTol) {
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
                HyphalAnalyser analyser = new HyphalAnalyser(objProc, imageRes, imageBox, objBox);
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
            //perimPoints = (int) Math.round(polyObjRoi.getLength());
            xCent = xSum / pixArea;
            yCent = ySum / pixArea;
            double[] xcoordinates = new double[polyObjRoi.getNCoordinates()];
            double[] ycoordinates = new double[polyObjRoi.getNCoordinates()];
            for (int n = 0; n < xcoordinates.length; n++) {
                xcoordinates[n] = polyObjRoi.getXCoordinates()[n];
                ycoordinates[n] = polyObjRoi.getYCoordinates()[n];
            }
//            Plot plot1 = new Plot("Original", "x", "y", xcoordinates, ycoordinates);
//            plot1.show();
            Pixel boundPoints[] = DSPProcessor.getDistanceSignal(polyObjRoi.getNCoordinates(),
                    xCent, yCent, polyObjRoi.getXCoordinates(),
                    polyObjRoi.getYCoordinates(), imageRes);
            double dist[] = new double[boundPoints.length];
            for (int i = 0; i < boundPoints.length; i++) {
                dist[i] = boundPoints[i].getZ();
            }
            double[] upscaledDistInput = DSPProcessor.upScale(dist);
            double sampleRate = (1.0d / imageRes)
                    * upscaledDistInput.length / dist.length;
            double distfracparams[] = DSPProcessor.calcFourierDim(DSPProcessor.calcFourierSpec(upscaledDistInput, sampleRate), sampleRate, 1.0);
            if (distfracparams != null) {
//                double truncdistfracparams[] = DSPProcessor.calcFourierDim(DSPProcessor.calcFourierSpec(upscaledDistInput, sampleRate), sampleRate, 0.25);
                distfracDim = distfracparams[2];
//                distfracDimFit = distfracparams[3];
//                truncdistfracDim = truncdistfracparams[2];
//                truncdistfracDimFit = truncdistfracparams[3];
            } else {
                distfracDim = Double.NaN;
//                distfracDimFit = Double.NaN;
            }

            //double[][] curvePoints = DSPProcessor.calcKappa(xcoordinates, ycoordinates, imageRes);
            double[] curvePoints = DSPProcessor.smoothSignal(upscaledDistInput, sampleRate);
            if (curvePoints != null) {
                //Plot plot2 = new Plot("Smoothed", "x", "y", curvePoints[0], curvePoints[1]);
                //plot2.show();
                double index[] = new double[curvePoints.length];
                for (int g = 0; g < curvePoints.length; g++) {
                    index[g] = g;
                }
                //Plot plot1 = new Plot("Original", "ArcLength", "Radius", index, upscaledDistInput);
                //plot1.show();
                //Plot plot3 = new Plot("Smoothed", "Arclength", "Radius", index, curvePoints);
                //plot3.show();
                /*
                 * double[] upscaledKappa =
                 * DSPProcessor.upScale(curvePoints[2]); double kappaSampleRate
                 * = (1.0d / imageRes) upscaledKappa.length /
                 * curvePoints[2].length;
                 */
//                double smoothedfracparams[] = DSPProcessor.calcFourierDim(DSPProcessor.calcFourierSpec(curvePoints, sampleRate), sampleRate, 1.0);
//                if (smoothedfracparams != null) {
//                    smoothedfracDim = smoothedfracparams[2];
//                    smoothedfracDimFit = smoothedfracparams[3];
//                } else {
//                    smoothedfracDim = Double.NaN;
//                    smoothedfracDimFit = Double.NaN;
//                }
            }
            params[4] = dist.length;
        }

        params[0] = objCirc;
        params[1] = objArea;
        params[2] = distfracDim;
        params[3] = lac;
//        params[5] = smoothedfracDim;
//        params[6] = distfracDimFit;
//        params[7] = smoothedfracDimFit;
//        params[8] = truncdistfracDim;
//        params[9] = truncdistfracDimFit;

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
                resultsTable.addValue(LENGTH_HEAD, (totalLength * imageRes));
            }
            if ((outputData & NUMBER_OF_ENDPOINTS) != 0) {
                resultsTable.addValue(TIP_HEAD, numEnds);
            }
            if ((outputData & HYPHAL_GROWTH_UNIT) != 0) {
                resultsTable.addValue(HGU_HEAD, (growthUnit * imageRes));
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
    public void initialise(String format, double length, double minArea, double maxArea,
            double minCirc, double maxCirc, double lacTol, double resolution, double confidence) {
        minBranchLength = length;
        this.minArea = minArea;
        this.maxCirc = maxCirc;
        this.maxArea = maxArea;
        this.minCirc = minCirc;
        this.lacTol = lacTol;
        imageFormat = format;
        confInterval = confidence;
        imageRes = resolution;
        imageResolution2 = imageRes * imageRes;
        useMorphFilters = true;

        return;
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

    /**
     * Returns a String representation of this BatchAnalyser object.
     */
    public String toString() {
        return "\nCurrent Working Directory: " + currentDirectory
                + "\nImage Format: " + imageFormat
                + "\nMinimum Branch Length: " + minBranchLength
                + "\nMaximum Circularity: " + maxCirc
                + "\nMinimum Area: " + minArea
                + "\nLacunarity Tolerance: " + lacTol
                + "\nConfidence Interval: " + confInterval
                + "\nCreate Mask Images: " + createMaskImages
                + "\nSubtract Background: " + subtractBackground
                + "\nLight Background: " + lightBackground
                + "\nOutput Data: " + outputData
                + "\nManual Threshold: " + manualThreshold
                + "\nImage Resolution: " + imageRes;
    }

    /**
     * Returns true if <i>object</i> is this BatchAnalyser object, false
     * otherwise.
     */
    public boolean equals(Object object) {
        if (object == null) {
            return false;
        } else if (getClass() != object.getClass()) {
            return false;
        } else {
            Batch_Analyser otherAnamorf = (Batch_Analyser) object;
            return (imageFormat.equals(otherAnamorf.imageFormat)
                    && minBranchLength == otherAnamorf.minBranchLength
                    && maxCirc == otherAnamorf.maxCirc
                    && minArea == otherAnamorf.minArea
                    && lacTol == otherAnamorf.lacTol
                    && confInterval == otherAnamorf.confInterval
                    && createMaskImages == otherAnamorf.createMaskImages
                    && subtractBackground == otherAnamorf.subtractBackground
                    && lightBackground == otherAnamorf.lightBackground
                    && outputData == otherAnamorf.outputData
                    && manualThreshold == otherAnamorf.manualThreshold
                    && imageRes == otherAnamorf.imageRes);
        }
    }
}
