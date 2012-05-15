package AnaMorf;

/**
 * Estimations of erf(x) and erf<sup>-1</sup>(x), based on the paper entitled "A
 * handy approximation for the error function and its inverse" by Sergei
 * Winitzki:<br>
 * <br>
 * http://homepages.physik.uni-muenchen.de/~Winitzki/erf-approx.pdf
 */
public class ErrorFunction {
    private final static double a = (8.0 * (Math.PI - 3.0)) /
            ((3.0 * Math.PI) * (4.0 - Math.PI));

    /**
     * Returns erf(x).
     */
    public static double erf(double x) {
        double x2 = Math.pow(x, 2.0);

        double num = (4.0 / Math.PI) + (a * x2);
        double denom = 1.0 + (a * x2);
        double expo = -x2 * num / denom;
        return (x / Math.abs(x)) * Math.sqrt(1.0 - Math.exp(expo));
    }

    /**
     * Returns erf<sup>-1</sup>(x).
     */
    public static double erfInverse(double x){
        double b = 2.0 / (Math.PI * a);
        double c = (Math.log(1.0 - Math.pow(x, 2.0))) / 2.0;
        double d = b + c;
        double e = Math.log(1.0 - Math.pow(x, 2.0)) / a;
        double f = Math.sqrt(Math.pow(d, 2.0) - e);

        return Math.sqrt(-b -c + f);
    }

    /**
     * Evaluates the cumulative normal distribution function &Phi(x).
     */
    public static double phi(double x) {
        return 0.5 * (1.0 + erf(x / (Math.sqrt(2.0))));
    }

    /**
     * Evaluates the inverse cumulative normal distribution function
     * &Phi<sup>-1</sup>(x).
     */
    public static double phiInverse(double x) {
        return Math.sqrt(2.0) * erfInverse(2.0 * x - 1.0);
    }
}