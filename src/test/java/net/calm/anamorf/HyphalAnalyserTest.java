package net.calm.anamorf;

import ij.process.ByteProcessor;
import org.junit.jupiter.api.Test;

import java.awt.Rectangle;

import static org.junit.jupiter.api.Assertions.assertEquals;

class HyphalAnalyserTest {

    @Test
    void analysesStraightLineLengthTipsAndHgu() {
        ByteProcessor skeleton = new ByteProcessor(20, 20);
        skeleton.setValue(255);
        skeleton.fill();
        for (int x = 5; x <= 15; x++) {
            skeleton.putPixel(x, 10, 0);
        }

        HyphalAnalyser analyser = new HyphalAnalyser(
                skeleton, 1.0, new Rectangle(0, 0, 20, 20), new Rectangle(0, 0, 20, 20));
        analyser.analyse();

        assertEquals(11, analyser.getLength());
        assertEquals(2, analyser.getTips());
        assertEquals(0, analyser.getBranchpoints());
        assertEquals(5.5, analyser.getHGU(), 1e-9);
    }
}
