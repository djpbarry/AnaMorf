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
import net.calm.iaclasslibrary.IAClasses.DataStatistics;

/**
 * Summarises results presented in ImageJ's Results Table. Data is identified
 * using the column headings specified in <i>BatchAnalyser</i>.
 */
public class ResultSummariser implements Measurements {

    private static double alpha = 0.05;
    private ResultsTable results;

    /**
     * Constructs a new ResultsSummariser object using the specified confidence
     * interval, assuming the value is greater than zero and less than 100.
     * Otherwise, a default value of 95% is used.
     */
    public ResultSummariser(double confidenceInterval) {
        if (confidenceInterval > 0 && confidenceInterval < 100) {
            alpha = 1.0 - confidenceInterval;
        } else {
            alpha = 0.05;
        }
    }

    /**
     * Summarises the data in ImageJ's results table according to the column
     * headings specified in <i>BatchAnalyser</i>.
     */
    public void summarise() {
        int row, areaIndex, circIndex, fracIndex, lacIndex, hguIndex,
                lengthIndex, tipIndex, branchIndex;

        Analyzer analyserObject = new Analyzer();
        results = Analyzer.getResultsTable();
        if (results.getCounter() < 1) {
            return;
        }

        int cols = results.getLastColumn();
        
//        areaIndex = results.getColumnIndex(Batch_Analyser.AREA_HEAD);
//        circIndex = results.getColumnIndex(Batch_Analyser.CIRC_HEAD);
//        fracIndex = results.getColumnIndex(Batch_Analyser.FRAC_HEAD);
//        lacIndex = results.getColumnIndex(Batch_Analyser.LAC_HEAD);
        hguIndex = results.getColumnIndex(Batch_Analyser.HGU_HEAD);
//        lengthIndex = results.getColumnIndex(Batch_Analyser.LENGTH_HEAD);
//        tipIndex = results.getColumnIndex(Batch_Analyser.TIP_HEAD);
//        branchIndex = results.getColumnIndex(Batch_Analyser.BRANCH_HEAD);

        row = results.getCounter();
        results.incrementCounter();
        results.setLabel("Mean", row);
        results.incrementCounter();
        results.setLabel("Standard Deviation", row + 1);
        results.incrementCounter();
        results.setLabel((1.0 - alpha) + "% Confidence Interval", row + 2);

        for(int i=1; i<=cols; i++){
            printStats(row, i, i==hguIndex);
        }
        
//        printStats(row, areaIndex, false);
//        printStats(row, circIndex, false);
//        printStats(row, fracIndex, false);
//        printStats(row, lacIndex, false);
//        printStats(row, hguIndex, true);
//        printStats(row, lengthIndex, false);
//        printStats(row, tipIndex, false);
//        printStats(row, branchIndex, false);

        analyserObject.displayResults();
        analyserObject.updateHeadings();
    }

    /**
     * Analyses the data contained in the column of ImageJ's results table
     * specified by <i>columnIndex</i>. The mean value is printed at
     * <i>rowNumber</i> and the confidence interval at <i>rowNumber + 1</i>. If
     * <i>cleanData</i> is true, only non-zero values are included in the
     * analysis.
     */
    public void printStats(int rowNumber, int columnIndex, boolean cleanData) {
        int dataLength;

        if (columnIndex < 0) {
            return;
        }
        float data[] = results.getColumn(columnIndex);
        if (cleanData) {
            dataLength = removeZeros(data, rowNumber);
        } else {
            dataLength = rowNumber;
        }
        DataStatistics stats = new DataStatistics(alpha, data, dataLength);
        results.setValue(columnIndex, rowNumber, stats.getMean());
        results.setValue(columnIndex, (rowNumber + 1), stats.getStdDev());
//        results.setValue(columnIndex, (rowNumber + 2), stats.getConfidenceInterval());
    }

    private int removeZeros(float data[], int size) {
        int i, j;
        float temp[] = data;

        for (i = 0, j = 0; i < size; i++) {
            if (temp[i] > 0) {
                data[j] = temp[i];
                j++;
            }
        }
        return j;
    }
}
