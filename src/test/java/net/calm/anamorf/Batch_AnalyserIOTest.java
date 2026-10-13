package net.calm.anamorf;

import ij.measure.ResultsTable;
import net.calm.anamorf.params.DefaultParams;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertTrue;

class Batch_AnalyserIOTest {

    @TempDir
    Path tempDir;

    @Test
    void saveResultsWritesIso8859Csv() throws IOException {
        ResultsTable table = new ResultsTable();
        table.incrementCounter();
        table.addLabel("image_0");
        table.addValue("Area (µm²)", 10.0);
        table.addValue("Circularity", 0.5);
        table.incrementCounter();
        table.addLabel("image_1");
        table.addValue("Area (µm²)", 20.0);
        table.addValue("Circularity", 0.75);

        Batch_Analyser analyser = new Batch_Analyser(true, null, new DefaultParams());
        analyser.resultsTable = table;

        analyser.saveResults(tempDir.toFile());

        byte[] bytes = Files.readAllBytes(tempDir.resolve("results.csv"));
        String text = new String(bytes, StandardCharsets.ISO_8859_1);

        assertTrue(text.contains("Area (µm²)"), "expected µ heading in CSV");
        assertTrue(text.contains("image_0"));
        assertTrue(text.contains("image_1"));

        boolean hasIso8859Mu = false;
        for (byte b : bytes) {
            if ((b & 0xFF) == 0xB5) {
                hasIso8859Mu = true;
                break;
            }
        }
        assertTrue(hasIso8859Mu, "expected ISO-8859-1 µ byte (0xB5) in CSV");
    }
}
