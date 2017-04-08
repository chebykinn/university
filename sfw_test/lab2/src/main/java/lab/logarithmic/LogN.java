package lab.logarithmic;

import lab.AbstractFunction;
import lab.Functions;

/**
 * Created by daituganov on 08.04.17.
 */
public class LogN extends AbstractFunction {

    public LogN(Double precision) {
        super(precision);
    }
    @Override
    protected double calculate(double arg) {
        return 0;
    }

    protected double log(double arg, int base) {
        if (base < 0 || base == 1) {
            throw new IllegalArgumentException();
        }
        Ln ln = new Ln(getPrecision());

        if (Math.abs(arg - base) < DELTA) {
            return 1d;
        }

        if (Math.abs(arg - 1d) < DELTA) {
            return 0d;
        }

        return ln.calc(arg) / ln.calc(base);
    }
}
