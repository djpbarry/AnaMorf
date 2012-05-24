package AnaMorf;

import ij.measure.CurveFitter;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * Calculates the mean, standard deviation, confidence interval, minimum and
 * maximum value of a data set.
 *
 * @author David J Barry <davejpbarry@gmail.com>
 * @version 05OCT2010
 */
public class DataStatistics {

    private final int dataSize;
    private double mean = 0.0;
    private double median = 0.0;
    private double confidenceInterval = 0.0;
    private double standardDeviation = 0.0;
    private double upper99 = 0.0;
    private double lower99 = 0.0;
    private double minValue = Double.POSITIVE_INFINITY;
    private double maxValue = Double.NEGATIVE_INFINITY;
    private final double alpha;
    private int nans;
    private ArrayList<Integer> zerocrossings = null;

    public DataStatistics(double a, double data[][], int n) {
        if (a > 0 && a < 100) {
            alpha = a;
        } else {
            alpha = 0.05;
        }
        dataSize = n;
        if (n < 1) {
            return;
        }
        double newdata[] = new double[n];
        for (int i = 0; i < data.length; i++) {
            System.arraycopy(data[i], 0, newdata, data.length * i, data[i].length);
        }
        nans = 0;
        Arrays.sort(newdata);
        median = newdata[n / 2];
        int upper99index = (int) Math.round(n * 0.99);
        if (upper99index >= newdata.length) {
            upper99index = newdata.length - 1;
        }
        upper99 = newdata[upper99index];
        lower99 = newdata[(int) Math.round(n * 0.01)];
        calcMean(newdata);
        calcStdDev(newdata);
        calcConfInt();
    }

    /**
     * Constructs a new DataStatistics object.
     *
     * @param a 100 - confidence level to be applied in the analysis
     * @param data the data to be analysed.
     * @param n the size of the data array.
     */
    public DataStatistics(double a, double data[], int n) {
        if (a > 0 && a < 100) {
            alpha = a;
        } else {
            alpha = 0.05;
        }
        dataSize = n;
        if (n < 1) {
            return;
        }
        nans = 0;

        Arrays.sort(data);
        median = data[n / 2];
        int upper99index = (int) Math.round(n * 0.99);
        if (upper99index >= data.length) {
            upper99index = data.length - 1;
        }
        upper99 = data[upper99index];
        lower99 = data[(int) Math.round(n * 0.05)];
        calcMean(data);
        calcStdDev(data);
        calcConfInt();
    }

    public DataStatistics(double a, float data[], int n) {
        this(a, floatArrayToDouble(data), n);
    }

    private void calcMean(double data[]) {
        int i;
        double sum = 0.0d;
        zerocrossings = new ArrayList<Integer>();

        for (i = 0; i < dataSize; i++) {
            if (!(Double.isInfinite(data[i]) || Double.isNaN(data[i]))) {
                sum += data[i];
                if (data[i] > maxValue) {
                    maxValue = data[i];
                }
                if (data[i] < minValue) {
                    minValue = data[i];
                }
                if (i < dataSize - 1 && (data[i] * data[i + 1] <= 0.0)) {
                    zerocrossings.add(new Integer(i));
                }
            } else {
                nans++;
            }
        }
        mean = sum / (dataSize - nans);
    }

    private void calcStdDev(double data[]) {
        int i;
        double sumvar = 0.0d;

        for (i = 0; i < dataSize; i++) {
            if (!(Double.isInfinite(data[i]) || Double.isNaN(data[i]))) {
                sumvar += Math.pow(data[i] - mean, 2);
            }
        }

        double variance = sumvar / (dataSize - nans);

        standardDeviation = Math.sqrt(variance);
    }

    private void calcConfInt() {
        confidenceInterval = ErrorFunction.phiInverse(1.0 - alpha / 2.0)
                * standardDeviation / Math.sqrt(dataSize);
    }

    public double getMean() {
        return mean;
    }

    public double getMedian() {
        return median;
    }

    public double getLower99() {
        return lower99;
    }

    public double getUpper99() {
        return upper99;
    }

    public double getStdDev() {
        return standardDeviation;
    }

    public double getConfidenceInterval() {
        return confidenceInterval;
    }

    public double getMin() {
        return minValue;
    }

    public double getMax() {
        return maxValue;
    }

    public ArrayList<Integer> getZerocrossings() {
        return zerocrossings;
    }

    public double[] findBestRegression(double[] data, int startindex, int endindex, double tol) {
        double sumMeanDiffSqr = 0.0;
        double index[] = new double[dataSize];
        for (int x = 0; x < dataSize; x++) {
            sumMeanDiffSqr += Math.pow(data[x] - mean, 2);
            index[x] = x;
        }
        double p[] = {0.0, 0.0, 0.0, startindex, endindex};
        while (p[2] < tol && (int) Math.round(p[4]) - (int) Math.round(p[3]) > 10) {
            double truncdata[] = new double[(int) Math.round(p[4]) - (int) Math.round(p[3]) + 1];
            double truncindex[] = new double[(int) Math.round(p[4]) - (int) Math.round(p[3]) + 1];
            System.arraycopy(data, (int) Math.round(p[3]), truncdata, 0, truncdata.length);
            System.arraycopy(index, (int) Math.round(p[3]), truncindex, 0, truncindex.length);
            CurveFitter fitter = new CurveFitter(truncindex, truncdata);
            fitter.doFit(CurveFitter.STRAIGHT_LINE);
            p[0] = fitter.getParams()[0];
            p[1] = fitter.getParams()[1];
            p[2] = getRSquared((int) Math.round(p[3]), (int) Math.round(p[4]), data, p, sumMeanDiffSqr);
            p[3] += 1.0;
            p[4] -= 1.0;
        }
        return p;
    }

    double getRSquared(int startindex, int endindex, double[] data, double p[], double sumMeanDiffSqr) {
        double rSquared = 0.0;
        if (sumMeanDiffSqr > 0.0) {
            double srs = sumResiduals(startindex, endindex, data, p);
            rSquared = 1.0 - srs / sumMeanDiffSqr;
        }
        return rSquared;
    }

    double sumResiduals(int startindex, int endindex, double[] data, double[] p) {
        double res = 0.0;
        for (int i = startindex; i < endindex; i++) {
            double e = evaluate(p, i) - data[i];
            res += (e * e);
        }
        return res;
    }

    double evaluate(double[] p, double x) {
        return p[0] + p[1] * x;
    }

    public static double[] floatArrayToDouble(float array[]) {
        double newArray[] = new double[array.length];
        for (int i = 0; i < array.length; i++) {
            newArray[i] = array[i];
        }
        return newArray;
    }
}
