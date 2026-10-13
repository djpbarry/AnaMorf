package net.calm.anamorf;

import net.calm.anamorf.params.DefaultParams;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class Batch_AnalyserTest {

    @Test
    void getFileExtensionsAliasesTifAndTiff() {
        assertArrayEquals(new String[]{"tif", "tiff"}, Batch_Analyser.getFileExtensions("TIF"));
        assertArrayEquals(new String[]{"tif", "tiff"}, Batch_Analyser.getFileExtensions("TIFF"));
        assertArrayEquals(new String[]{"tif", "tiff"}, Batch_Analyser.getFileExtensions("tiff"));
    }

    @Test
    void getFileExtensionsAliasesJpgAndJpeg() {
        assertArrayEquals(new String[]{"jpg", "jpeg"}, Batch_Analyser.getFileExtensions("JPG"));
        assertArrayEquals(new String[]{"jpg", "jpeg"}, Batch_Analyser.getFileExtensions("JPEG"));
        assertArrayEquals(new String[]{"jpg", "jpeg"}, Batch_Analyser.getFileExtensions("jpeg"));
    }

    @Test
    void getFileExtensionsPassesThroughOtherFormats() {
        assertArrayEquals(new String[]{"PNG"}, Batch_Analyser.getFileExtensions("PNG"));
        assertArrayEquals(new String[]{"BMP"}, Batch_Analyser.getFileExtensions("BMP"));
        assertArrayEquals(new String[]{"GIF"}, Batch_Analyser.getFileExtensions("GIF"));
    }

    @Test
    void getOptionsOrderMatchesFlags() {
        DefaultParams props = new DefaultParams();
        props.setProperty(DefaultParams.PROJ_AREA_LABEL, "true");
        props.setProperty(DefaultParams.CIRC_LABEL, "true");
        props.setProperty(DefaultParams.MEAN_BRANCH_LABEL, "false");
        props.setProperty(DefaultParams.TOT_LENGTH_LABEL, "true");
        props.setProperty(DefaultParams.NUM_END_LABEL, "false");
        props.setProperty(DefaultParams.NUM_BRANCH_LABEL, "false");
        props.setProperty(DefaultParams.FOURIER_FRAC_LABEL, "true");
        props.setProperty(DefaultParams.BOX_COUNT_LABEL, "false");
        props.setProperty(DefaultParams.LAC_LABEL, "true");
        props.setProperty(DefaultParams.CURVE_LABEL, "false");

        boolean[] options = new Batch_Analyser(true, null, props).getOptions();

        assertArrayEquals(
                new boolean[]{true, true, false, true, false, false, true, false, true, false},
                options);
    }

    @Test
    void toOutputDataSumsPowersOfTwo() {
        boolean[] options = {true, false, true, false, false, false, false, false, false, false};
        assertEquals(1 + 4, Batch_Analyser.toOutputData(options));
    }

    @Test
    void toOutputDataEmptyAndFull() {
        assertEquals(0, Batch_Analyser.toOutputData(new boolean[10]));

        boolean[] allTrue = new boolean[10];
        Arrays.fill(allTrue, true);
        assertEquals(1023, Batch_Analyser.toOutputData(allTrue)); // 2^10 - 1
    }

    @Test
    void flagConstantsArePowersOfTwoInOrder() {
        assertEquals(1, Batch_Analyser.AREAS);
        assertEquals(2, Batch_Analyser.CIRC);
        assertEquals(4, Batch_Analyser.HYPHAL_GROWTH_UNIT);
        assertEquals(8, Batch_Analyser.TOTAL_HYPHAL_LENGTH);
        assertEquals(16, Batch_Analyser.NUMBER_OF_ENDPOINTS);
        assertEquals(32, Batch_Analyser.NUMBER_OF_BRANCHES);
        assertEquals(64, Batch_Analyser.FOURIER_FRACTAL_DIMENSION);
        assertEquals(128, Batch_Analyser.BOX_FRACTAL_DIMENSION);
        assertEquals(256, Batch_Analyser.LACUNARITY);
        assertEquals(512, Batch_Analyser.CURVATURE);
    }

    @Test
    void getVersionReadsFilteredResource() {
        String version = Batch_Analyser.getVersion();
        assertNotNull(version);
        assertFalse(version.isEmpty());
        assertFalse(version.equals("unknown"));
    }
}
