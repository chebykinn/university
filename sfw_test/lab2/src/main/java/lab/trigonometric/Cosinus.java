package lab.trigonometric;

import lab.AbstractFunction;
import lab.Functions;
import lab.util.FactorialSeries;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;

import static java.lang.Double.*;
import static java.lang.Math.PI;

/**
 * Created by ivan on 07.04.17.
 */
public class Cosinus extends AbstractFunction{

    {
        table.put(-PI, -1.0);
        table.put(-PI / 2, 0.0);
        table.put(0.0, 1.0);
        table.put(PI / 2, 0.0);
        table.put(PI, -1.0);

        table.put(3 * PI / 4, -0.707106781);
        table.put( PI / 4, 0.707106781);
        table.put(-PI / 4, -0.707106781);
        function = Functions.COSINUS;
    }

    public Cosinus(Double precision) {
        super(precision);
    }

    @Override
    protected double calculate(double arg) {

        if (isNaN(arg) || isInfinite(arg)) {
            return NaN;
        }

        arg = subOverages(arg);

        int scale = 10;
        double d = DELTA;

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

        double valueToDouble = value.setScale(scale, RoundingMode.UP).doubleValue();

        if(valueToDouble > 1) valueToDouble = 1;
        else if(valueToDouble < -1) valueToDouble = -1;
        return valueToDouble;
    }

    protected static double subOverages(double arg) {
        long periodCounter = (long) (arg / (2 * PI)) + 1;

        if(arg > PI || arg < -PI)
            arg -= periodCounter * 2 * PI;
        return arg;
    }
}
