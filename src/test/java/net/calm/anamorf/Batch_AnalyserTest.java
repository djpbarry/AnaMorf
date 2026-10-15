package net.calm.anamorf;

import java.io.File;

import net.calm.anamorf.params.DefaultParams;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;

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
    void getFileExtensionsLowercasesOtherFormats() {
        assertArrayEquals(new String[]{"png"}, Batch_Analyser.getFileExtensions("PNG"));
        assertArrayEquals(new String[]{"bmp"}, Batch_Analyser.getFileExtensions("BMP"));
        assertArrayEquals(new String[]{"gif"}, Batch_Analyser.getFileExtensions("GIF"));
        assertArrayEquals(new String[]{"png"}, Batch_Analyser.getFileExtensions("png"));
    }

    @Test
    void computeOutputDataMapsLabelsToFlags() {
        DefaultParams props = new DefaultParams();
        String[] labels = {DefaultParams.PROJ_AREA_LABEL, DefaultParams.CIRC_LABEL,
                DefaultParams.MEAN_BRANCH_LABEL, DefaultParams.TOT_LENGTH_LABEL,
                DefaultParams.NUM_END_LABEL, DefaultParams.NUM_BRANCH_LABEL,
                DefaultParams.FOURIER_FRAC_LABEL, DefaultParams.BOX_COUNT_LABEL,
                DefaultParams.LAC_LABEL, DefaultParams.CURVE_LABEL};
        for (String label : labels) {
            props.setProperty(label, "false");
        }

        assertEquals(0, Batch_Analyser.computeOutputData(props));

        props.setProperty(DefaultParams.PROJ_AREA_LABEL, "true");
        props.setProperty(DefaultParams.MEAN_BRANCH_LABEL, "true");
        props.setProperty(DefaultParams.CURVE_LABEL, "true");

        assertEquals(Batch_Analyser.AREAS | Batch_Analyser.HYPHAL_GROWTH_UNIT | Batch_Analyser.CURVATURE,
                Batch_Analyser.computeOutputData(props));
    }

    @Test
    void computeOutputDataFull() {
        DefaultParams props = new DefaultParams();
        assertEquals(1023, Batch_Analyser.computeOutputData(props));
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

    @Test
    void getPropsReturnsNonNullSharedProperties() {
        assertNotNull(Batch_Analyser.getProps());
    }

    @Test
    void constructorPublishesPropsToGetProps() {
        DefaultParams props = new DefaultParams();
        new Batch_Analyser(true, new File("."), props);
        assertSame(props, Batch_Analyser.getProps());
    }
}
