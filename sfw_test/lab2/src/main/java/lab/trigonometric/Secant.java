package lab.trigonometric;

import lab.AbstractFunction;
import lab.Functions;
import lab.util.BigDecimalSqrt;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;

import static java.lang.Double.NaN;
import static java.lang.Double.POSITIVE_INFINITY;
import static java.lang.Math.PI;

/**
 * Created by ivan on 07.04.17.
 */
public class Secant extends AbstractFunction{
    {
//        table.put(-PI, -1.0000001352604628);
        table.put(-PI, -1.0);
        table.put(-PI / 2, Double.POSITIVE_INFINITY);
        table.put(0.0, 1.0);
        table.put(PI / 2, Double.POSITIVE_INFINITY);
//        table.put(PI, -1.0000001352604628);
        table.put(PI, -1.0);

        table.put(3 * PI / 4, -1.41421356375);
        table.put(-3 * PI / 4, -1.41421356375);
        table.put( PI / 4, 1.41421356375);
        table.put(-PI / 4, 1.41421356375);
        function = Functions.SECANT;
    }

    Cosinus cos;

    public Secant(double precision) {
        super(precision);
        cos = new Cosinus(precision);
    }

    @Override
    public void setPrecision(double precision){
        super.setPrecision(precision);
        cos.setPrecision(precision);
    }

    @Override
    protected double calculate(double arg) {
        /*
        if (Math.abs(arg - Math.PI) < DELTA ) {
            return 0d;
        } else if (Math.abs(arg + Math.PI) < DELTA ) {
            return 0d;
        } else if (Math.abs(arg) < DELTA ) {
            return 0d;
        }
        */
        if (Math.abs(arg - Math.PI/2) < DELTA) {
            return POSITIVE_INFINITY;
        }
        if (Math.abs(arg + Math.PI/2) < DELTA) {
            return POSITIVE_INFINITY;
        }
//        if (Math.abs(arg - 2*Math.PI) < DELTA) {
//            return 0d;
//        }
//        if (Math.abs(arg + 2*Math.PI) < DELTA) {
//            return 0d;
//        }
//        if (Math.abs(arg - 3*Math.PI/2) < DELTA) {
//            return POSITIVE_INFINITY;
//        }
//        if (Math.abs(arg + 3*Math.PI/2) < DELTA) {
//            return POSITIVE_INFINITY;
//        }
        if( Double.isNaN(arg) || Double.isInfinite(arg) ){
            return NaN;
        }
        int scale = 10;
        BigDecimal last;
        BigDecimal value = new BigDecimal(0d, MathContext.UNLIMITED);
        int n = scale;

        double cosVal = cos.calc(arg);
        do {
            last = value;
            value = new BigDecimal(1d, MathContext.UNLIMITED)
                    .divide(new BigDecimal(cosVal, MathContext.UNLIMITED), n, RoundingMode.HALF_UP);
            n++;
        } while (getPrecision() <= value.subtract(last).abs().doubleValue() && n < MAX_ITERATIONS);
        return value.setScale(n, RoundingMode.UP).doubleValue();
//        return 1 / cos.calc(arg);
    }
}
