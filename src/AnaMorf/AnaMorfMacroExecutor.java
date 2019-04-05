/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package AnaMorf;

import IO.PropertyWriter;
import UtilClasses.GenUtils;
import ij.macro.ExtensionDescriptor;
import ij.macro.MacroExtension;
import java.io.File;
import params.DefaultParams;

/**
 *
 * @author David Barry <david.barry at crick dot ac dot uk>
 */
public class AnaMorfMacroExecutor implements MacroExtension {

    private final String[] extensionFunctionNames = new String[]{"runAnaMorf"};

    public AnaMorfMacroExecutor(){
        
    }
    
    public ExtensionDescriptor[] getExtensionFunctions() {
        return new ExtensionDescriptor[]{
            new ExtensionDescriptor(extensionFunctionNames[0], new int[]{
                MacroExtension.ARG_STRING, MacroExtension.ARG_STRING
            }, this)
        };
    }

    public String handleExtension(String name, Object[] args) {
        if (name.contentEquals(extensionFunctionNames[0])) {
            if (!(args[0] instanceof String && args[1] instanceof String)) {
                System.out.print(String.format("Error: arguments passed to %s are not valid.", extensionFunctionNames[0]));
                return "";
            }
            DefaultParams props = new DefaultParams();
            try {
                PropertyWriter.loadProperties(props, null, new File((String) args[1]));
            } catch (Exception e) {
                GenUtils.logError(e, "Failed to load AnaMorf properties file.");
            }
            (new Batch_Analyser(true, new File((String) args[0]), props)).run(null);
        }
        return null;
    }

}
