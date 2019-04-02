/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package AnaMorf;

import ij.WindowManager;
import ij.macro.ExtensionDescriptor;
import ij.macro.MacroExtension;

/**
 *
 * @author David Barry <david.barry at crick dot ac dot uk>
 */
public class AnaMorfMacroExecutor implements MacroExtension {

    private final String[] extensionFunctionNames = new String[]{"runTemplateMatcher"};

    public ExtensionDescriptor[] getExtensionFunctions() {
        return new ExtensionDescriptor[]{
            new ExtensionDescriptor(extensionFunctionNames[0], new int[]{
                MacroExtension.ARG_STRING, MacroExtension.ARG_STRING, MacroExtension.ARG_NUMBER
            }, this)
        };
    }

    public String handleExtension(String name, Object[] args) {
        if (name.contentEquals(extensionFunctionNames[0])) {
            if (!(args[0] instanceof String && args[1] instanceof String && args[2] instanceof Double)) {
                System.out.print(String.format("Error: arguments passed to %s are not valid.", extensionFunctionNames[0]));
                return "";
            }
//            this.image = WindowManager.getImage((String) args[0]).getImageStack();
//            this.template = WindowManager.getImage((String) args[1]).getProcessor();
//            this.threshold = (Double) args[2];
//            run();
        }
        return null;
    }

}
