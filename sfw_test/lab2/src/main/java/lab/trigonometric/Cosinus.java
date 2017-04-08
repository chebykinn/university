package lab.trigonometric;

import lab.AbstractFunction;
import lab.util.FactorialSeries;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;

import static java.lang.Double.*;

/**
 * Created by ivan on 07.04.17.
 */
public class Cosinus extends AbstractFunction{

    {
        table.put(0.0, 0.0);
    }

    public Cosinus(boolean isStub, double precision) {
        super(isStub, precision);
    }

    @Override
    protected double calculate(double arg) {

        if (isNaN(arg) || isInfinite(arg)) {
            return NaN;
        }

        long periodCounter = (long) (arg / (2 * Math.PI));

        if(arg > Math.PI || arg < -Math.PI)
            arg -= periodCounter;

        int scale = 0;
        double d = DELTA;

        for(; d >= 1; scale++) {
            d /= 10;
        }

        BigDecimal last;
        BigDecimal value = new BigDecimal(0d, MathContext.UNLIMITED);
        int n = 0;

        do {
            last = value;
            value = value.add((new BigDecimal(-1, MathContext.UNLIMITED).pow(n)).
                    multiply((new BigDecimal(arg, MathContext.UNLIMITED).pow(2 * n))).
                    divide(new BigDecimal(FactorialSeries.factorial(2 * n)), scale, RoundingMode.HALF_UP));
            n++;
        } while (getPrecision() <= value.subtract(last).abs().doubleValue() && n < MAX_ITERATIONS);

        double valueToDouble = value.setScale(++scale, RoundingMode.UP).doubleValue();

        if(valueToDouble > 1) valueToDouble = 1;
        else if(valueToDouble < -1) valueToDouble = -1;
        return valueToDouble;
    }
}
