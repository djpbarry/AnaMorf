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
        (new AnaMorfMacroExecutor()).handleExtension("runAnaMorf", new String[]{"D:/debugging/anamorf_debug/AnaMorf Test 3",
            "D:/debugging/anamorf_debug/properties.xml"});
//        SkeletonPruner sp = new SkeletonPruner(100, (ByteProcessor) (IJ.openImage()).getProcessor().convertToByteProcessor());
//
//        IJ.saveAs(new ImagePlus("Pruned", sp.getPrunedImage()), "PNG", "D:\\OneDrive - The Francis Crick Institute\\Working Data\\Tapon\\Maxine\\Tiffs\\Pruned");
        System.exit(0);
    }
}
