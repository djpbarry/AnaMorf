/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package net.calm.anamorf;

import net.calm.anamorf.params.DefaultParams;
import ij.IJ;
import ij.macro.ExtensionDescriptor;
import ij.macro.MacroExtension;
import net.calm.iaclasslibrary.IO.PropertyWriter;
import net.calm.iaclasslibrary.UtilClasses.GenUtils;

import java.io.File;
import java.util.Properties;

/**
 * @author David Barry <david.barry at crick dot ac dot uk>
 */
public class AnaMorfMacroExecutor implements MacroExtension {

    private final String INITIALISE = "initialiseAnaMorf";
    private final String SET_FILE_TYPE = "setAnaMorfFileType";
    private final String SET_CURVATURE = "setAnaMorfCurvatureWindow";
    private final String SET_MIN_BRANCH = "setAnaMorfMinBranchLength";
    private final String RUN = "runAnaMorf";
    private final String RESET_PARAMS = "resetParameters";
    private Batch_Analyser ba;

    public AnaMorfMacroExecutor() {

    }

    public ExtensionDescriptor[] getExtensionFunctions() {
        return new ExtensionDescriptor[]{
                new ExtensionDescriptor(INITIALISE, new int[]{
                        MacroExtension.ARG_STRING, MacroExtension.ARG_STRING
                }, this),
                new ExtensionDescriptor(SET_FILE_TYPE, new int[]{
                        MacroExtension.ARG_STRING
                }, this),
                new ExtensionDescriptor(SET_CURVATURE, new int[]{
                        MacroExtension.ARG_NUMBER
                }, this),
                new ExtensionDescriptor(SET_MIN_BRANCH, new int[]{
                        MacroExtension.ARG_NUMBER
                }, this),
                new ExtensionDescriptor(RUN, new int[0], this),
                new ExtensionDescriptor(RESET_PARAMS, new int[0], this)};
    }

    public String handleExtension(String name, Object[] args) {
        switch (name) {
            case INITIALISE:
                initialise(args);
                break;
            case SET_FILE_TYPE:
                setFileType(args);
                break;
            case SET_CURVATURE:
                setCurvature(args);
                break;
            case SET_MIN_BRANCH:
                setMinBranchLength(args);
                break;
            case RUN:
                run();
                break;
            case RESET_PARAMS:
                resetParams();
                break;
            default:
                IJ.log(String.format("Error: %s is not a valid command.", name));
        }
        return null;
    }

    void initialise(Object[] args) {
        if (!(args[0] instanceof String && args[1] instanceof String)) {
            invalidArguments();
            return;
        }
        DefaultParams props = new DefaultParams();
        try {
            PropertyWriter.loadProperties(props, null, new File((String) args[1]));
        } catch (Exception e) {
            GenUtils.logError(e, "Failed to load AnaMorf properties file.");
        }
        ba = new Batch_Analyser(true, new File((String) args[0]), props);
    }

    void setFileType(Object[] args) {
        if (!(args[0] instanceof String)) {
            invalidArguments();
            return;
        }
        Batch_Analyser.getProps().setProperty(DefaultParams.IMAGE_FORMAT_LABEL, (String) args[0]);
    }

    void setCurvature(Object[] args) {
        if (!(args[0] instanceof Number)) {
            invalidArguments();
            return;
        }
        Batch_Analyser.getProps().setProperty(DefaultParams.CURVE_LABEL, "true");
        Batch_Analyser.getProps().setProperty(DefaultParams.CURVE_WIN_LABEL, String.valueOf(args[0]));
    }

    void setMinBranchLength(Object[] args) {
        if (!(args[0] instanceof Number)) {
            invalidArguments();
            return;
        }
        Batch_Analyser.getProps().setProperty(DefaultParams.MIN_BRANCH_LABEL, String.valueOf(args[0]));
    }

    void run() {
        ba.run(null);
    }

    void resetParams(){
        Properties props = Batch_Analyser.getProps();
        Batch_Analyser.getProps().setProperty(DefaultParams.BOX_COUNT_LABEL, "false");
        Batch_Analyser.getProps().setProperty(DefaultParams.CIRC_LABEL, "false");
        Batch_Analyser.getProps().setProperty(DefaultParams.CURVE_LABEL, "false");
        Batch_Analyser.getProps().setProperty(DefaultParams.FOURIER_FRAC_LABEL, "false");
        Batch_Analyser.getProps().setProperty(DefaultParams.LAC_LABEL, "false");
        Batch_Analyser.getProps().setProperty(DefaultParams.MEAN_BRANCH_LABEL, "false");
        Batch_Analyser.getProps().setProperty(DefaultParams.NUM_BRANCH_LABEL, "false");
        Batch_Analyser.getProps().setProperty(DefaultParams.NUM_END_LABEL, "false");
        Batch_Analyser.getProps().setProperty(DefaultParams.PROJ_AREA_LABEL, "false");
        Batch_Analyser.getProps().setProperty(DefaultParams.TOT_LENGTH_LABEL, "false");
    }

    void invalidArguments() {
        IJ.log("Arguments are invalid");
    }

}
