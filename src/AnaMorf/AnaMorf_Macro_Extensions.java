/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package AnaMorf;

import ij.IJ;
import ij.macro.Functions;
import ij.plugin.PlugIn;

/**
 *
 * @author David Barry <david.barry at crick dot ac dot uk>
 */
public class AnaMorf_Macro_Extensions implements PlugIn {

    public void run(String args) {
        if (!IJ.macroRunning()) {
            IJ.error("Macro extensions are designed to be run from within a macro."
                    + "\n Instructions on how to do so will follow.");
            IJ.log(toString());
            return;
        } else {
            Functions.registerExtensions(new AnaMorfMacroExecutor());
        }
    }

    public String toString() {
        return "To gain access to AnaMorf Macro extensions from within a macro, put\n"
                + " the following line at the beginning of your macro:\n"
                + "\n"
                + "run(\"AnaMorf Macro Extensions\");\n"
                + "\n"
                + "This will enable the following macro functions:\n"
                + "\n"
                + "Ext.runAnaMorf(Input_Directory, Properties);\n"
                + "-- Runs AnaMorf on the specified input directory with the\n"
                + " specified properties.xml file.";
    }
}
