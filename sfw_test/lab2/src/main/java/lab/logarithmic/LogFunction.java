package lab.logarithmic;

import lab.Calculation;

import static java.lang.Math.pow;

/**
 * Created by daituganov on 08.04.17.
 */
public class LogFunction implements Calculation {
    private double precision;

    public LogFunction(double precision) {
        this.precision = precision;
    }
    Ln ln = new Ln(precision);
    Log2 log2 = new Log2(precision);
    Log10 log10 = new Log10(precision);

    @Override
    public double calc(double arg) {
        return (pow(pow(ln.calc(arg) - log10.calc(arg), 2) / log2.calc(arg), 3) * log2.calc(arg));
    }
}
