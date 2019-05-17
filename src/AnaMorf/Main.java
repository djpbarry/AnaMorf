/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package AnaMorf;

public class Main {

    public static void main(String args[]) {
//        Batch_Analyser ba = new Batch_Analyser();
//        ba.run(null);
        AnaMorfMacroExecutor exec = new AnaMorfMacroExecutor();
        exec.handleExtension("initialiseAnaMorf", new String[]{"C:\\Users\\barryd\\Desktop\\test", "C:\\Users\\barryd\\Desktop\\problem2_properties.xml"});
        exec.handleExtension("setAnaMorfFileType", new String[]{"PNG"});
        exec.handleExtension("setAnaMorfCurvatureWindow", new Double[]{new Double(7)});
        exec.handleExtension("setAnaMorfMinBranchLength", new Double[]{new Double(8)});
        exec.handleExtension("runAnaMorf", new String[]{});

//        SkeletonPruner sp = new SkeletonPruner(100, (ByteProcessor) (IJ.openImage()).getProcessor().convertToByteProcessor());
//
//        IJ.saveAs(new ImagePlus("Pruned", sp.getPrunedImage()), "PNG", "D:\\OneDrive - The Francis Crick Institute\\Working Data\\Tapon\\Maxine\\Tiffs\\Pruned");
        System.exit(0);
    }
}
