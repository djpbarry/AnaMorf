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

import Binary.BinaryMaker;
import Curvature.CurveAnalyser;
import UtilClasses.Utilities;
import IAClasses.DSPProcessor;
import IAClasses.FractalEstimator;
import IAClasses.OnlyExt;
import IAClasses.Pixel;
import IO.DataWriter;
import IO.PropertyWriter;
import Revision.Revision;
import Thresholding.FuzzyThresholder;
import UtilClasses.GenUtils;
import UtilClasses.GenVariables;
import ij.IJ;
import ij.ImagePlus;
import ij.Prefs;
import ij.gui.PolygonRoi;
import ij.gui.Roi;
import ij.gui.Wand;
import ij.measure.Measurements;
import ij.measure.ResultsTable;
import ij.plugin.PlugIn;
import ij.plugin.filter.Analyzer;
import ij.plugin.filter.BackgroundSubtracter;
import ij.plugin.filter.EDM;
import ij.plugin.filter.GaussianBlur;
import ij.process.AutoThresholder;
import ij.process.Blitter;
import ij.process.ByteBlitter;
import ij.process.ByteProcessor;
import ij.process.ColorBlitter;
import ij.process.ColorProcessor;
import ij.process.FloatProcessor;
import ij.process.FloodFiller;
import ij.process.ImageProcessor;
import ij.process.ImageStatistics;
import ij.process.TypeConverter;
import java.awt.Color;
import java.awt.Polygon;
import java.awt.Rectangle;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Properties;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;
import org.apache.commons.math3.stat.descriptive.SummaryStatistics;
import params.DefaultParams;

/**
 * BatchAnalyser is designed to automatically analyse a batch of images of
 * filamentous microbes and output data on parameters such as object area,
 * circularity, lacunarity, fractal dimension, total hyphal length, total number
 * of tips and hyphal growth unit. A detailed algorithmic description can be
 * found in the following publications:<br> <br> D. Barry, C. Chan, and G.
 * Williams, “Morphological quantification of filamentous fungal development
 * using membrane immobilization and automatic image analysis,” <i>J Ind
 * Microbiol Biot</i>, vol. 36, no. 6, pp. 787-800, 2009.<br> <br> D. J. Barry,
 * “Quantifying the branching frequency of virtual filamentous microbes using
 * fractal analysis,” <i>Biotech Bioeng</i>, vol. 110, no. 2, pp. 437–447, 2013.
 */
public class Batch_Analyser implements PlugIn {

    private final double MIN_CIRC = 0.0, MAX_AREA = Double.MAX_VALUE; // Morphological thresholds used during analysis
    private boolean outputResults, useMorphFilters;
    private int outputData = 0; // Determines what metrics will be output to Results Table
    private static final int FOREGROUND = 0, BACKGROUND = 255; // Values for foreground & background pixels
    private ByteProcessor maskImage, refProc;
    private ColorProcessor colorSkelImage;
    private ImageProcessor bwSkelImage;
    private FloatProcessor curveMap;
    private String imageName;
    public String title = String.format("AnaMorf v%d.%s", Revision.VERSION, new DecimalFormat("000").format(Revision.revisionNumber));
    ResultsTable resultsTable;
    private File currentDirectory;
    private DescriptiveStatistics wholeImageCurvature;
    ArrayList<ArrayList<Double>> cumulativeCurveStats;
    ArrayList<String> cumulativeCurveStatsLabels;
    private static Properties props;
    private final boolean macroMode;

    static {
        if (props == null) {
            IJ.log("Initialising properties...");
            props = new DefaultParams();
        }
    }

    /*
     * Column headings used for Results Table output
     */
    public static final String CIRC_HEAD = "Circularity",
            AREA_HEAD = "Area (microns^2)",
            TOT_AREA_HEAD = "Total Area (microns^2)",
            FOUR_FRAC_HEAD = "Fourier Fractal Dimension",
            BOX_FRAC_HEAD = "Box-Counting Fractal Dimension",
            LAC_HEAD = "Lacunarity",
            LENGTH_HEAD = "Total Length (microns)",
            TIP_HEAD = "Endpoints",
            HGU_HEAD = "HGU (microns)",
            BRANCH_HEAD = "Branchpoints",
            CURVE_HEAD = "Curvature";

    /*
     * Flags for results output
     */
    public static final int AREAS = 1,
            CIRC = 2,
            HYPHAL_GROWTH_UNIT = 4,
            TOTAL_HYPHAL_LENGTH = 8,
            NUMBER_OF_ENDPOINTS = 16,
            NUMBER_OF_BRANCHES = 32,
            FOURIER_FRACTAL_DIMENSION = 64,
            BOX_FRACTAL_DIMENSION = 128,
            LACUNARITY = 256,
            CURVATURE = 512;

//    public Batch_Analyser(boolean wholeImage) {
//        this.wholeImage = wholeImage;
//        noEdge = false;
//    }
    public Batch_Analyser() {
        this(false, null, null);
    }

    public Batch_Analyser(boolean macroMode, File currentDirectory, Properties props) {
        this.macroMode = macroMode;
        this.currentDirectory = currentDirectory;
        Batch_Analyser.props = props;
    }

    /**
     * Implementation of {@link PlugIn}'s run method.
     *
     * @param arg passed by ImageJ.
     */
    public void run(String arg) {
        Prefs.blackBackground = false;
        if (!macroMode && !showGUI()) {
            return;
        }
        setOutputs();
        try {
            if (!macroMode) {
                currentDirectory = Utilities.getFolder(new File(props.getProperty(DefaultParams.INPUT_DIR)), null, true);
            }
        } catch (Exception e) {
            GenUtils.logError(e, "Could not open directory.");
            return;
        }
        if (currentDirectory == null) {
            return;
        }
        props.setProperty(DefaultParams.INPUT_DIR, currentDirectory.getAbsolutePath());
        IJ.log(title);
        IJ.log(currentDirectory.getAbsolutePath());
        resultsTable = Analyzer.getResultsTable();
        resultsTable.reset();
        resultsTable.incrementCounter();
        resultsTable.addLabel(currentDirectory.getAbsolutePath());
        long startTime = System.currentTimeMillis();
        File resultsDirectory = new File(GenUtils.openResultsDirectory(currentDirectory.getAbsolutePath() + File.separator + title));
        if (analyseFiles(currentDirectory, resultsDirectory)) {
            try {
                saveResults(resultsDirectory);
                if ((outputData & CURVATURE) != 0 && Boolean.parseBoolean(props.getProperty(DefaultParams.OUTPUT_CURVE_LABEL))) {
                    DataWriter.saveValues(cumulativeCurveStats,
                            new File(String.format("%s%s%s", resultsDirectory.getAbsolutePath(), File.separator, "CurvatureValues.csv")),
                            new String[]{"Image", "X", "Y", "Theta 1", "Theta 2"}, cumulativeCurveStatsLabels.toArray(new String[]{}), false);
                }
            } catch (IOException e) {
                GenUtils.logError(e, "Could not save results file.");
            }
        }
        try {
            PropertyWriter.saveProperties(props, resultsDirectory.getAbsolutePath(), title, true);
        } catch (Exception e) {
            GenUtils.logError(e, "Failed to save property file.");
        }
        IJ.log(title + " done: " + ((double) (System.currentTimeMillis() - startTime)) / 1000.0 + " s");
    }

    void saveResults(File resultsDirectory) throws IOException {
        CSVPrinter printer = new CSVPrinter(new OutputStreamWriter(new FileOutputStream(new File(resultsDirectory + File.separator + "results.csv")), GenVariables.ISO), CSVFormat.EXCEL);
        printer.printRecord((Object[]) resultsTable.getHeadings());
        int n = resultsTable.size();
        int m = resultsTable.getLastColumn();
        for (int i = 0; i < n; i++) {
            printer.print(resultsTable.getLabel(i));
            for (int j = 0; j <= m; j++) {
                printer.print(resultsTable.getStringValue(j, i));
            }
            printer.println();
        }
        printer.close();
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
    public boolean analyseFiles(File directory, File resultsDirectory) {
        FilenameFilter directoryFilter = new OnlyExt(props.getProperty(DefaultParams.IMAGE_FORMAT_LABEL));
        String imageFilenames[] = directory.list(directoryFilter); // Generates a list of image filenames of the format specified by the user
        if (imageFilenames.length < 1) {
            IJ.showMessage("'" + directory + "' contains no images of type ." + props.getProperty(DefaultParams.IMAGE_FORMAT_LABEL));
            return false;
        }
        /*
         * A folder for storing mask images is created if required
         */
        for (int i = 0; i < imageFilenames.length; i++) {
            IJ.showProgress(i, imageFilenames.length);
            useMorphFilters = true;
            outputResults = !Boolean.parseBoolean(props.getProperty(DefaultParams.WHOLE_IMAGE_LABEL));
            imageName = imageFilenames[i];
            IJ.log("Scanning " + imageName);
            ImagePlus currImage = new ImagePlus(directory + File.separator + imageName);
            IJ.log(String.format("Analysing image %d of %d - %s", i + 1, imageFilenames.length, currImage.getShortTitle()));
            try {
                analyseFile(currImage, resultsDirectory);
            } catch (Exception e) {
                GenUtils.logError(e, String.format("Problem analysing %s.", imageName));
            }
        }
        return true;
    }

    void analyseFile(ImagePlus currImage, File resultsDirectory) throws Exception {
        if (!(currImage.getProcessor() instanceof ColorProcessor)) {
            if (currImage.isInvertedLut()) {
                IJ.log("It looks like this image has an inverted LUT - the analysis may not work properly.");
            }
            int width = currImage.getWidth();
            int height = currImage.getHeight();
            maskImage = new ByteProcessor(width, height);
            curveMap = new FloatProcessor(width, height);
            curveMap.setValue(0.0);
            curveMap.fill();
            maskImage.setColor(BACKGROUND);
            maskImage.fill();
            colorSkelImage = new ColorProcessor(width, height);
            wholeImageCurvature = new DescriptiveStatistics();
            /*
             * Reference used to ensure that each object is only analysed once
             */
            refProc = new ByteProcessor(width, height);
            if (refProc.isInvertedLut()) {
                refProc.invertLut();
            }
            refProc.setValue(BACKGROUND);
            refProc.fill();
            ImageProcessor preProcessedImage;
            if (Boolean.parseBoolean(props.getProperty(DefaultParams.PRE_PROCESS_LABEL))) {
                preProcessedImage = preProcessImage(currImage.duplicate());
            } else if (!BinaryMaker.checkIfBinary(currImage)) {
                GenUtils.logError(null, "Aborting - an 8-bit binary image is required.");
                return;
            } else {
                preProcessedImage = currImage.getProcessor().duplicate();
            }
            searchImage(preProcessedImage, Boolean.parseBoolean(props.getProperty(DefaultParams.EXCLUDE_EDGE_LABEL)), null);
            ImagePlus maskOutput = new ImagePlus(imageName + " - Mask", maskImage.duplicate());
            if (Boolean.parseBoolean(props.getProperty(DefaultParams.CREATE_MASK_LABEL))) {
                IJ.saveAs(maskOutput, "png", resultsDirectory + "//" + maskOutput.getTitle());
            }
            if (Boolean.parseBoolean(props.getProperty(DefaultParams.WHOLE_IMAGE_LABEL)) && maskOutput != null) {
                outputResults = true;
                useMorphFilters = false;
                ByteProcessor wholeImageMask = (ByteProcessor) maskImage.duplicate();
                wholeImageMask.setValue(BACKGROUND);
                wholeImageMask.fill();
                analyseImage(wholeImageMask, maskImage, null, Boolean.parseBoolean(props.getProperty(DefaultParams.EXCLUDE_EDGE_LABEL)), null);
            }
            if (((outputData & HYPHAL_GROWTH_UNIT) != 0) || ((outputData & NUMBER_OF_ENDPOINTS) != 0)
                    || ((outputData & TOTAL_HYPHAL_LENGTH) != 0) || ((outputData & CURVATURE) != 0)) {
                ImagePlus skelOutput = new ImagePlus(imageName + " - Skeleton", colorSkelImage);
                ImagePlus curveOutput = new ImagePlus(imageName + " - Curve Map", curveMap);
                if (Boolean.parseBoolean(props.getProperty(DefaultParams.CREATE_MASK_LABEL))) {
                    IJ.saveAs(skelOutput, "png", resultsDirectory + "//" + skelOutput.getTitle());
                    IJ.saveAs(curveOutput, "tif", resultsDirectory + "//" + curveOutput.getTitle());
                }
            }
        } else {
            IJ.log("Greyscale images required - " + imageName + " will not be analysed.");
        }
    }

    private ByteProcessor preProcessImage(ImagePlus currentImage) {
        ImageProcessor currentProcessor = currentImage.getProcessor();
        if (currentProcessor == null) {
            IJ.log("There was a problem reading " + currentImage.getTitle());
            return null;
        }
        double filterRadius = Double.parseDouble(props.getProperty(DefaultParams.NOISE_RED_LABEL)) / Double.parseDouble(props.getProperty(DefaultParams.IMAGE_RES_LABEL));

        if (!Boolean.parseBoolean(props.getProperty(DefaultParams.LIGHT_BACK_LABEL))) {
            currentProcessor.invert();
        }
        if (currentProcessor instanceof FloatProcessor) {
            currentProcessor = (new TypeConverter(currentProcessor, true)).convertToShort();
        }
        /*
         * Low-frequency noise removal
         */
        if (Boolean.parseBoolean(props.getProperty(DefaultParams.REMOVE_BACK_LABEL))) {
            (new BackgroundSubtracter()).rollingBallBackground(currentProcessor,
                    Double.parseDouble(props.getProperty(DefaultParams.BACK_FILT_LABEL)) / Double.parseDouble(props.getProperty(DefaultParams.IMAGE_RES_LABEL)), false, true, false,
                    true, false);
        }
        (new GaussianBlur()).blurGaussian(currentProcessor, filterRadius, filterRadius, 0.01);
        /*
         * Generate binary image
         */
        if (Boolean.parseBoolean(props.getProperty(DefaultParams.AUTO_THRESH_LABEL))) {
            FuzzyThresholder ft = new FuzzyThresholder(currentProcessor.duplicate(),
                    AutoThresholder.Method.valueOf(props.getProperty(DefaultParams.THRESH_METH_LABEL)), 0.00);
            currentProcessor = ft.threshold();
            currentProcessor.erode();
            currentProcessor.dilate();
        } else {
            currentProcessor.threshold(Integer.parseInt(props.getProperty(DefaultParams.THRESH_LEV_LABEL)));
        }
        if (Boolean.parseBoolean(props.getProperty(DefaultParams.SEPARATE_TOUCHING_LABEL))) {
            currentProcessor.invert();
            (new EDM()).toWatershed(currentProcessor);
            currentProcessor.invert();
        }
        return (ByteProcessor) (new TypeConverter(currentProcessor, false)).convertToByte();
    }

    /**
     * Displays a dialog to obtain input parameters from a user.
     *
     * @return false if the dialog was closed/exited by the user, true otherwise
     */
    public boolean showGUI() {
        boolean valid = false;
        if (Batch_Analyser.props == null) {
            Batch_Analyser.props = new DefaultParams();
        }
        while (!valid) {
            valid = true;
            UserInterface gui = new UserInterface(IJ.getInstance(), true, title, props);
            gui.setVisible(true);
            if (!gui.exitProgram()) {
                return true;
            } else {
                return false;
            }
        }
        return true;
    }

    private void setOutputs() {
        boolean options[] = getOptions();
        for (int n = 0; n < options.length; n++) {
            if (options[n]) {
                outputData += (int) Math.round(Math.pow(2.0, n));
            }
        }
    }

    /**
     * Searches the image (represented by <code>currentImage</code>) for
     * objects, traces the outline of each object using {@link Wand}
     * <code>.autoOutline()</code> and sends each detected boundary to
     * <code>analyseImage()</code> for morphological analysis.
     */
    public int searchImage(ImageProcessor binaryProcessor, boolean excludeEdges, Roi roi) throws Exception {
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
        if (maskImage == null && Boolean.parseBoolean(props.getProperty(DefaultParams.CREATE_MASK_LABEL))) {
            maskImage = new ByteProcessor(width, height);
            maskImage.setColor(BACKGROUND);
            maskImage.fill();
        }
        Rectangle bounds = roi.getBounds();
        /*
         * Ensure image is not inverted
         */
//        if (checkGreyLevels) {
//            binaryProcessor = ensureGreyValues(binaryProcessor, false);
//        }
        byte pixels[] = (byte[]) binaryProcessor.getPixels();
        ImageProcessor filledBP = binaryProcessor.duplicate();
        fill(filledBP, FOREGROUND, BACKGROUND);
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
        double imageRes = Double.parseDouble(props.getProperty(DefaultParams.IMAGE_RES_LABEL));
        double imageRes2 = imageRes * imageRes;
        int x, y, pixArea = 0, numEnds = 0, numBranches = 0;
        int minPixLength = (int) Math.round(Double.parseDouble(props.getProperty(DefaultParams.MIN_BRANCH_LABEL)) / Double.parseDouble(props.getProperty(DefaultParams.IMAGE_RES_LABEL)));
        double var, meanSq, objArea, objCirc, xCent, yCent, curvature = Double.NaN;
        double objectPerim = 1.0, lac = 1.0, distfracDim = Double.NaN;
        double xSum = 0.0, ySum = 0.0, growthUnit = 0.0, totalLength = 0.0;
        Rectangle objBox, imageBox = new Rectangle(0, 0, binProc.getWidth(), binProc.getHeight());
        double boxFracDims[] = null;
        if (objRoi != null) {
            Polygon poly = objRoi.getPolygon();
            objBox = poly.getBounds();
        } else {
            objBox = new Rectangle(0, 0, objMask.getWidth(), objMask.getHeight());
        }
        if ((outputData & BOX_FRACTAL_DIMENSION) != 0) {
            IJ.log(String.format("Calculating %s", DefaultParams.BOX_COUNT_LABEL));
            if (Boolean.parseBoolean(props.getProperty(DefaultParams.WHOLE_IMAGE_LABEL))) {
                boxFracDims = (new FractalEstimator()).do2DEstimate(binProc);
            } else {
                binProc.setRoi(objBox);
                boxFracDims = (new FractalEstimator()).do2DEstimate(binProc.crop());
            }
        }
        IJ.log(String.format("Calculating %s", DefaultParams.PROJ_AREA_LABEL));
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
                    }
                    /*
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
        objArea = pixArea * imageRes2;
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
            if ((objArea < Double.parseDouble(props.getProperty(DefaultParams.MIN_AREA_LABEL))) || (objArea > MAX_AREA)
                    || (objCirc < MIN_CIRC) || (objCirc > Double.parseDouble(props.getProperty(DefaultParams.MAX_CIRC_LABEL)))) {
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
        }

        if (((outputData & HYPHAL_GROWTH_UNIT) != 0) || ((outputData & NUMBER_OF_ENDPOINTS) != 0)
                || ((outputData & TOTAL_HYPHAL_LENGTH) != 0) || ((outputData & LACUNARITY) != 0)
                || ((outputData & CURVATURE) != 0)) {
            /*
             * An estimate of the object's lacunarity is used to determine
             * whether an accurate evaluation of hyphal length and number of
             * hyphal tips is possible.
             */
            IJ.log(String.format("Calculating %s", DefaultParams.LAC_LABEL));
            binProc.setMask(objMask); // Restrict calculation of lacunarity to within object boundary
            binProc.invert(); // Foreground pixels = 255, to ensure non-zero mean
            ImageStatistics objStats = ImageStatistics.getStatistics(binProc,
                    Measurements.MEAN + Measurements.STD_DEV, null);
            var = Math.pow(objStats.stdDev, 2);
            meanSq = Math.pow(objStats.mean, 2);
            lac = Math.abs((var / meanSq) - 1.0);
            if (((outputData & HYPHAL_GROWTH_UNIT) != 0) || ((outputData & NUMBER_OF_ENDPOINTS) != 0)
                    || ((outputData & TOTAL_HYPHAL_LENGTH) != 0) || ((outputData & CURVATURE) != 0)) {
                binProc.invert(); // Reverse inversion above
                /*
                 * Draw a white border around object to ensure no 'contact'
                 * between object and image boundary
                 */
                try {
                    IJ.log("Skeletonising...");
                    ByteProcessor objProc = (ByteProcessor) binProc.createProcessor((objBox.width + 4), (objBox.height + 4));
                    objProc.setValue(BACKGROUND);
                    objProc.fill();
                    objProc.insert(binProc, 2, 2);
                    objBox = new Rectangle(objBox.x - 2, objBox.y - 2, objBox.width + 4, objBox.height + 4);
                    /*
                 * Skeletonise image for determinations of hyphal length and tip
                 * number
                     */
                    objProc.skeletonize();
                    /*
                 * Prune image to remove artefacts of skeletonisation
                     */
                    IJ.log("Pruning skeleton...");
                    SkeletonPruner pruner1 = new SkeletonPruner(minPixLength, objProc, objBox, false, false);
                    objProc = pruner1.getPrunedImage();
//                    IJ.saveAs(new ImagePlus("", objProc), "PNG", "D:\\debugging\\anamorf_debug\\objProc");
//                SkeletonPruner pruner2 = new SkeletonPruner(0, (ByteProcessor) objProc.duplicate(), objBox, true, true);
                    IJ.log(String.format("Calculating %s", DefaultParams.CURVE_LABEL));
                    curvature = generateCurveMap(new HyphalAnalyser(objProc.duplicate(), Double.parseDouble(props.getProperty(DefaultParams.IMAGE_RES_LABEL)), imageBox, objBox).findLongestPath(), imageRoiBounds.width, imageRoiBounds.height, curveMap, (int) Math.round(Double.parseDouble(props.getProperty(DefaultParams.CURVE_WIN_LABEL))));
                    if (!Double.isNaN(curvature)) {
                        wholeImageCurvature.addValue(curvature);
                    }
                    IJ.log("Analysing skeleton...");
                    HyphalAnalyser analyser = new HyphalAnalyser(objProc, Double.parseDouble(props.getProperty(DefaultParams.IMAGE_RES_LABEL)), imageBox, objBox);
//                analyser.findLongestPath();
                    analyser.analyse(); // Analyse pruned skeleton
                    growthUnit = analyser.getHGU();
                    totalLength = analyser.getLength();
                    numEnds = analyser.getTips();
                    numBranches = analyser.getBranchpoints();
                    ColorBlitter skelBlit = new ColorBlitter(colorSkelImage);
                    skelBlit.copyBits(analyser.getColorOutput(), 0, 0, Blitter.ADD);
                    bwSkelImage = analyser.getBWOutput();
                    bwSkelImage.invert();
                } catch (Exception e) {
                    IJ.log("Problem analysing object in this image - skipping current object");
                    return false;
                }
            }
        }

        if ((outputData & FOURIER_FRACTAL_DIMENSION) != 0 && objRoi != null) {
            IJ.log(String.format("Calculating %s", DefaultParams.FOURIER_FRAC_LABEL));
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
                    polyObjRoi.getYCoordinates(), Double.parseDouble(props.getProperty(DefaultParams.IMAGE_RES_LABEL)));
            double dist[] = new double[boundPoints.length];
            for (int i = 0; i < boundPoints.length; i++) {
                dist[i] = boundPoints[i].getZ();
            }
            double[] upscaledDistInput = DSPProcessor.upScale(dist);
            double sampleRate = (1.0d / Double.parseDouble(props.getProperty(DefaultParams.IMAGE_RES_LABEL)))
                    * upscaledDistInput.length / dist.length;
            double distfracparams[] = DSPProcessor.calcFourierDim(DSPProcessor.calcFourierSpec(upscaledDistInput, sampleRate), sampleRate, 1.0);
            if (distfracparams != null) {
                distfracDim = (5.0 - Math.abs(distfracparams[1])) / 2.0;
            } else {
                distfracDim = Double.NaN;
            }
        }

        if (outputResults && (outputData != 0)) {
            /*
             * Output results to ImageJ's results table.
             */
            resultsTable.incrementCounter();
            if ((outputData & CIRC) != 0) {
                resultsTable.addValue(CIRC_HEAD, objCirc);
            }
            if ((outputData & AREAS) != 0) {
                double area = objArea;
                if (Boolean.parseBoolean(props.getProperty(DefaultParams.WHOLE_IMAGE_LABEL))) {
                    area = binProc.getStatistics().histogram[FOREGROUND] * imageRes2;
                }
                resultsTable.addValue(AREA_HEAD, area);
            }
            if ((outputData & FOURIER_FRACTAL_DIMENSION) != 0) {
                resultsTable.addValue(FOUR_FRAC_HEAD, distfracDim);
            }
            if ((outputData & LACUNARITY) != 0) {
                resultsTable.addValue(LAC_HEAD, lac);
            }
            if ((outputData & TOTAL_HYPHAL_LENGTH) != 0) {
                resultsTable.addValue(LENGTH_HEAD, (totalLength * Double.parseDouble(props.getProperty(DefaultParams.IMAGE_RES_LABEL))));
            }
            if ((outputData & NUMBER_OF_ENDPOINTS) != 0) {
                resultsTable.addValue(TIP_HEAD, numEnds);
            }
            if ((outputData & HYPHAL_GROWTH_UNIT) != 0) {
                resultsTable.addValue(HGU_HEAD, (growthUnit * Double.parseDouble(props.getProperty(DefaultParams.IMAGE_RES_LABEL))));
            }
            if ((outputData & NUMBER_OF_BRANCHES) != 0) {
                resultsTable.addValue(BRANCH_HEAD, numBranches);
            }
            if ((outputData & BOX_FRACTAL_DIMENSION) != 0) {
                if (boxFracDims != null) {
                    resultsTable.addValue(BOX_FRAC_HEAD, boxFracDims[0]);
                } else {
                    resultsTable.addValue(BOX_FRAC_HEAD, Double.NaN);
                }
            }
            if ((outputData & CURVATURE) != 0) {
                if (Boolean.parseBoolean(props.getProperty(DefaultParams.WHOLE_IMAGE_LABEL))) {
                    curvature = wholeImageCurvature.getMean();
                    double[] vals = wholeImageCurvature.getSortedValues();
                    vals = wholeImageCurvature.getValues();
                }
                resultsTable.addValue(CURVE_HEAD, curvature);
            }
            resultsTable.addLabel("Image", imageName);
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

    public void setOutputData(int outputData) {
        this.outputData = outputData;
    }

    public ByteProcessor getMaskImage() {
        return maskImage;
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

    double generateCurveMap(ArrayList<int[][]> branches, int width, int height, FloatProcessor curveMap, int window) {
        if (branches == null) {
            return Double.NaN;
        }
        if (cumulativeCurveStats == null) {
            cumulativeCurveStats = new ArrayList();
            cumulativeCurveStatsLabels = new ArrayList();
            for (int c = 0; c < 4; c++) {
                cumulativeCurveStats.add(new ArrayList<Double>());
            }
        }
        SummaryStatistics stats = new SummaryStatistics();
        for (int[][] branch : branches) {
            if (branch.length > 2 * window) {
                double[] curvature = CurveAnalyser.calcCurvature(branch, window, false, cumulativeCurveStats);
                for (int i = 0; i < curvature.length; i++) {
                    cumulativeCurveStatsLabels.add(imageName);
                    double c = Math.abs(curvature[i]);
                    curveMap.putPixelValue(branch[i + window][0], branch[i + window][1], c);
                    stats.addValue(c);
                }
            }
        }
        return stats.getMean();
    }

    public boolean[] getOptions() {
        return new boolean[]{Boolean.parseBoolean(props.getProperty(DefaultParams.PROJ_AREA_LABEL)),
            Boolean.parseBoolean(props.getProperty(DefaultParams.CIRC_LABEL)),
            Boolean.parseBoolean(props.getProperty(DefaultParams.MEAN_BRANCH_LABEL)),
            Boolean.parseBoolean(props.getProperty(DefaultParams.TOT_LENGTH_LABEL)),
            Boolean.parseBoolean(props.getProperty(DefaultParams.NUM_END_LABEL)),
            Boolean.parseBoolean(props.getProperty(DefaultParams.NUM_BRANCH_LABEL)),
            Boolean.parseBoolean(props.getProperty(DefaultParams.FOURIER_FRAC_LABEL)),
            Boolean.parseBoolean(props.getProperty(DefaultParams.BOX_COUNT_LABEL)),
            Boolean.parseBoolean(props.getProperty(DefaultParams.LAC_LABEL)),
            Boolean.parseBoolean(props.getProperty(DefaultParams.CURVE_LABEL))};
    }

    public static Properties getProps() {
        return props;
    }

}
