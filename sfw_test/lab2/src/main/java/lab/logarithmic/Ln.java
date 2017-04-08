package lab.logarithmic;

import lab.AbstractFunction;
import lab.Functions;

import java.util.Map;

import static java.lang.Double.*;

/**
 * Created by ivan on 07.04.17.
 */
public class Ln extends AbstractFunction {

    {
        table.put(0.0, 0.0);
        function = Functions.LN;
    }

    public Ln(Double precision) {
        super(precision);
    }

    @Override
    protected double calculate(double arg) {
        if (isNaN(arg) || arg < 0.0) {
            return NaN;
        }

        if (arg == POSITIVE_INFINITY) {
            return POSITIVE_INFINITY;
        }

        if (arg == 0.0) {
            return NEGATIVE_INFINITY;
        }

        double value = 0;
        double prevValue;
        int n = 1;
        int k = 1;
        if (Math.abs(arg - 1) <= 1) {
            do {
                prevValue = value;
                value -= ((Math.pow(-1, n) * Math.pow(arg - 1, n)) / n);
                n++;
            } while (getPrecision() <= Math.abs(value - prevValue) && n < MAX_ITERATIONS);
        } else {
            do {
                prevValue = value;
                value -= ((Math.pow(-1, k) * Math.pow(arg - 1, -k)) / k);
                k++;
            } while (getPrecision() <= Math.abs(value - prevValue) && k < MAX_ITERATIONS);
            value += calc(arg - 1);
        }

        return value;
    }
}
