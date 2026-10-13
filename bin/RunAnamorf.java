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
 * Usage: RunAnamorf <imagesDir> [workDir]
 *   imagesDir - directory of PNG images (src/test/resources/images)
 *   workDir   - optional directory to stage the greyscale images before
 *               analysis. Defaults to a fixed C:\anamorf-live so the
 *               results.csv directory label (and hence the baseline) is
 *               machine-independent.
 *
 * NOTE (unresolved): results.csv embeds the work directory's absolute path as
 * the first data label (Batch_Analyser.run adds the directory path as the
 * results-table label). This makes a naive SHA-256 baseline machine-specific.
 * The current workaround is a FIXED work directory (C:\anamorf-live), which is
 * a stopgap, not a fix: it hardcodes a Windows drive into a supposedly portable
 * tool. The proper fix is to normalise that directory-label row out of the hash
 * in BaselineTool (or stop writing the directory path as a data label in
 * Batch_Analyser.run). Left as a documented TODO pending a decision.
 */
public class RunAnamorf {

    public static void main(String[] args) throws Exception {
        if (args.length < 1) {
            System.err.println("Usage: RunAnamorf <imagesDir> [workDir]");
            System.exit(2);
        }
        Path imagesDir = Paths.get(args[0]);
        Path workDir = canonicalWorkDir(args.length > 1 ? args[1] : null);

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
    }

    /**
     * Returns a canonical, machine-independent work directory. The path is
     * embedded verbatim as the first label in results.csv (Batch_Analyser.run
     * adds the directory path as the results-table label), so it must be fixed
     * rather than derived from the checkout location, or every developer would
     * produce a different baseline.
     */
    private static Path canonicalWorkDir(String override) {
        if (override != null && !override.isEmpty()) {
            return Paths.get(override);
        }
        return Paths.get("C:", "anamorf-live");
    }
}
