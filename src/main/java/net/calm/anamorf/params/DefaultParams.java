/*
 * Copyright (C) 2018 David Barry <david.barry at crick dot ac dot uk>
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
package net.calm.anamorf.params;

import ij.IJ;
import java.util.Properties;

/**
 *
 * @author David Barry <david.barry at crick dot ac dot uk>
 */
public class DefaultParams extends Properties {

    public static final String IMAGE_FORMAT_LABEL = "Image Format";
    public static final String IMAGE_RES_LABEL = "Image Resolution (" + IJ.micronSymbol + "m/pixel)";
    public static final String MIN_BRANCH_LABEL = "Minimum Branch Length (" + IJ.micronSymbol + "m)";
    public static final String MAX_CIRC_LABEL = "Maximum Circularity";
    public static final String MIN_AREA_LABEL = "Minimum Area (" + IJ.micronSymbol + "m^2)";
    public static final String CURVE_WIN_LABEL = "Curvature Window";
    public static final String BOX_COUNT_LABEL = "Box-Counting Fractal Dimension";
    public static final String CIRC_LABEL = "Circularity";
    public static final String CURVE_LABEL = "Curvature";
    public static final String FOURIER_FRAC_LABEL = "Fourier Fractal Dimension";
    public static final String MEAN_BRANCH_LABEL = "Mean Branch Length";
    public static final String LAC_LABEL = "Lacunarity";
    public static final String NUM_BRANCH_LABEL = "Number of Branchpoints";
    public static final String NUM_END_LABEL = "Number of Endpoints";
    public static final String PROJ_AREA_LABEL = "Projected Area";
    public static final String TOT_LENGTH_LABEL = "Total Length";
    public static final String PRE_PROCESS_LABEL = "Pre-Process Images";
    public static final String LIGHT_BACK_LABEL = "Light Background";
    public static final String DARK_BACK_LABEL = "Dark Background";
    public static final String NOISE_RED_LABEL = "Noise Reduction Filter Radius (" + IJ.micronSymbol + "m)";
    public static final String REMOVE_BACK_LABEL = "Remove Background";
    public static final String BACK_FILT_LABEL = "Background Filter Radius (" + IJ.micronSymbol + "m^2)";
    public static final String AUTO_THRESH_LABEL = "Auto Threshold";
    public static final String MAN_THRESH_LABEL = "Manual Threshold";
    public static final String THRESH_METH_LABEL = "Threshold Method";
    public static final String THRESH_LEV_LABEL = "Threshold Level";
    public static final String CREATE_MASK_LABEL = "Create Mask Images";
    public static final String EXCLUDE_EDGE_LABEL = "Exclude Edge Objects";
    public static final String SEPARATE_TOUCHING_LABEL = "Separate Touching Objects";
    public static final String WHOLE_IMAGE_LABEL = "Treat Whole Image as One Object";
    public static final String OUTPUT_CURVE_LABEL = "Output Curvature Values";
    public static final String INPUT_DIR = "Input Directory";

    public DefaultParams() {
        this.setProperty(IMAGE_FORMAT_LABEL, "PNG");
        this.setProperty(IMAGE_RES_LABEL, "1.0");
        this.setProperty(MIN_BRANCH_LABEL, "10.0");
        this.setProperty(MAX_CIRC_LABEL, "1.0");
        this.setProperty(MIN_AREA_LABEL, "10.0");
        this.setProperty(CURVE_WIN_LABEL, "10");
        this.setProperty(BOX_COUNT_LABEL, "true");
        this.setProperty(CIRC_LABEL, "true");
        this.setProperty(CURVE_LABEL, "true");
        this.setProperty(FOURIER_FRAC_LABEL, "true");
        this.setProperty(MEAN_BRANCH_LABEL, "true");
        this.setProperty(LAC_LABEL, "true");
        this.setProperty(NUM_BRANCH_LABEL, "true");
        this.setProperty(NUM_END_LABEL, "true");
        this.setProperty(PROJ_AREA_LABEL, "true");
        this.setProperty(TOT_LENGTH_LABEL, "true");
        this.setProperty(PRE_PROCESS_LABEL, "true");
        this.setProperty(LIGHT_BACK_LABEL, "true");
        this.setProperty(DARK_BACK_LABEL, "false");
        this.setProperty(NOISE_RED_LABEL, "1.0");
        this.setProperty(REMOVE_BACK_LABEL, "true");
        this.setProperty(BACK_FILT_LABEL, "50.0");
        this.setProperty(AUTO_THRESH_LABEL, "true");
        this.setProperty(MAN_THRESH_LABEL, "false");
        this.setProperty(THRESH_METH_LABEL, "Default");
        this.setProperty(THRESH_LEV_LABEL, "0");
        this.setProperty(CREATE_MASK_LABEL, "true");
        this.setProperty(EXCLUDE_EDGE_LABEL, "true");
        this.setProperty(SEPARATE_TOUCHING_LABEL, "false");
        this.setProperty(WHOLE_IMAGE_LABEL, "false");
        this.setProperty(OUTPUT_CURVE_LABEL, "false");
        this.setProperty(INPUT_DIR, System.getProperty("user.dir"));
    }

}
