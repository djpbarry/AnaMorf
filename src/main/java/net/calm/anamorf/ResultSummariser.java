/* 
 * Copyright (C) 2015 David Barry <david.barry at cancer.org.uk>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package net.calm.anamorf;

import ij.measure.Measurements;
import ij.measure.ResultsTable;
import ij.plugin.filter.Analyzer;
import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;

/**
 * Summarises results presented in ImageJ's Results Table by appending mean and
 * population standard-deviation rows. Data is identified using the column
 * headings specified in <i>BatchAnalyser</i>.
 */
public class ResultSummariser implements Measurements {

    private ResultsTable results;

    /**
     * Constructs a new ResultsSummariser object. The confidence-interval
     * parameter is retained for API compatibility but is no longer used.
     */
    public ResultSummariser(double confidenceInterval) {
    }

    /**
     * Appends mean and standard-deviation rows to the ImageJ results table.
     */
    public void summarise() {
        Analyzer analyserObject = new Analyzer();
        results = Analyzer.getResultsTable();
        if (results.getCounter() < 1) {
            return;
        }

        int cols = results.getLastColumn();
        int hguIndex = results.getColumnIndex(Batch_Analyser.HGU_HEAD);

        int row = results.getCounter();
        results.incrementCounter();
        results.setLabel("Mean", row);
        results.incrementCounter();
        results.setLabel("Standard Deviation", row + 1);

        for (int i = 1; i <= cols; i++) {
            printStats(row, i, i == hguIndex);
        }

        analyserObject.displayResults();
        analyserObject.updateHeadings();
    }

    /**
     * Writes the mean and population standard deviation of the specified column
     * into the summary rows. When <i>cleanData</i> is true, only positive values
     * are included in the calculation.
     */
    public void printStats(int rowNumber, int columnIndex, boolean cleanData) {
        if (columnIndex < 0) {
            return;
        }
        float data[] = results.getColumn(columnIndex);
        int dataLength = cleanData ? removeZeros(data, rowNumber) : rowNumber;
        DescriptiveStatistics stats = new DescriptiveStatistics();
        for (int i = 0; i < dataLength; i++) {
            stats.addValue(data[i]);
        }
        results.setValue(columnIndex, rowNumber, stats.getMean());
        results.setValue(columnIndex, rowNumber + 1, Math.sqrt(stats.getPopulationVariance()));
    }

    int removeZeros(float data[], int size) {
        int j = 0;
        for (int i = 0; i < size; i++) {
            if (data[i] > 0) {
                data[j++] = data[i];
            }
        }
        return j;
    }
}
