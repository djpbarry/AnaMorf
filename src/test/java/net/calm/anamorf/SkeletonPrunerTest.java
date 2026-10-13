package net.calm.anamorf;

import ij.process.ByteProcessor;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class SkeletonPrunerTest {

    @Test
    void traceBranchReturnsLinePath() {
        ByteProcessor skeleton = new ByteProcessor(20, 20);
        skeleton.setValue(255);
        skeleton.fill();
        for (int x = 5; x <= 15; x++) {
            skeleton.putPixel(x, 10, 0);
        }

        SkeletonPruner pruner = new SkeletonPruner(1, skeleton);
        short[][] path = pruner.traceBranch(skeleton, 5, 10, new ArrayList<>(), 100);

        assertNotNull(path);
        assertEquals(11, path[0].length);
        assertEquals(11, path[1].length);
        for (int i = 0; i < 11; i++) {
            assertEquals(5 + i, path[0][i]);
            assertEquals(10, path[1][i]);
        }
    }
}
