import ij.IJ;
import ij.ImageJ;
import ij.ImagePlus;
import ij.process.ImageProcessor;
import net.calm.anamorf.Batch_Analyser;
import net.calm.anamorf.params.DefaultParams;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Headless runner for the AnaMorf live test (DEVELOPMENT_PLAN Phase C4).
 *
 * Converts the RGB regression images to 8-bit greyscale (AnaMorf rejects
 * colour input) and runs Batch_Analyser in macro mode over them. Results land
 * in a sibling "<title>_Output" directory containing results.csv.
 *
 * Usage: RunAnamorf <imagesDir> <workDir>
 *   imagesDir - directory of PNG images (src/test/resources/images)
 *   workDir   - directory to stage the greyscale images before analysis
 */
public class RunAnamorf {

    public static void main(String[] args) throws Exception {
        if (args.length < 2) {
            System.err.println("Usage: RunAnamorf <imagesDir> <workDir>");
            System.exit(2);
        }
        Path imagesDir = Paths.get(args[0]);
        Path workDir = Paths.get(args[1]);

        new ImageJ(ImageJ.NO_SHOW);

        Files.createDirectories(workDir);

        File[] images = imagesDir.toFile().listFiles(
                (dir, name) -> name.toLowerCase().endsWith(".png"));
        if (images == null || images.length == 0) {
            System.err.println("No PNG images in " + imagesDir);
            System.exit(1);
        }
        for (File image : images) {
            ImagePlus imp = IJ.openImage(image.getAbsolutePath());
            if (imp == null) {
                System.err.println("Failed to open " + image);
                System.exit(1);
            }
            ImageProcessor grey = imp.getProcessor().convertToByte(false);
            IJ.saveAs(new ImagePlus(image.getName(), grey), "png",
                    workDir.resolve(image.getName()).toString());
            imp.close();
        }

        DefaultParams props = new DefaultParams();
        props.setProperty(DefaultParams.IMAGE_FORMAT_LABEL, "PNG");

        Batch_Analyser analyser = new Batch_Analyser(true, workDir.toFile(), props);
        analyser.run(null);

        System.out.println("RunAnamorf complete; results.csv is in a sibling *_Output directory of " + workDir);

        // AnaMorf leaves ImageJ/AWT non-daemon threads running after main()
        // returns, which keeps the headless JVM alive (and hangs any calling
        // shell). Explicitly exit once the run is done.
        System.exit(0);
    }
}
