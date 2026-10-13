package net.calm.anamorf;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ResultSummariserTest {

    @Test
    void removeZerosCompactsPositiveValues() {
        float[] data = {1.0f, 0.0f, 2.5f, -1.0f, 3.0f, 0.0f};
        int count = new ResultSummariser(95.0).removeZeros(data, data.length);

        assertEquals(3, count);
        assertEquals(1.0f, data[0]);
        assertEquals(2.5f, data[1]);
        assertEquals(3.0f, data[2]);
    }

    @Test
    void removeZerosReturnsZeroWhenNoPositives() {
        float[] data = {0.0f, -1.0f, -2.0f};
        int count = new ResultSummariser(95.0).removeZeros(data, data.length);

        assertEquals(0, count);
    }
}
