package lab.trigonometric;

import lab.AbstractFunction;
import lab.Functions;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;

import static java.lang.Double.isInfinite;
import static java.lang.Double.isNaN;
import static java.lang.Float.NaN;
import static java.lang.Math.PI;

/**
 * Created by ivan on 07.04.17.
 */
public class Cotangent extends AbstractFunction {
    {
        table.put(-PI, Double.POSITIVE_INFINITY);
        table.put(-PI / 2, 0.0);
        table.put(0.0, Double.POSITIVE_INFINITY);
        table.put(PI / 2, 0.0);
        table.put(PI, Double.POSITIVE_INFINITY);

        table.put(3 * PI / 4, -1.0);
        table.put(-3 * PI / 4, 1.0);
        table.put( PI / 4, 1.0);
        table.put(-PI / 4, -1.0);
        function = Functions.COTANGENT;
    }

    Tangent tan;

    public Cotangent(double precision) {
        super(precision);
        tan = new Tangent(precision);
    }

    @Override
    public void setPrecision(double precision){
        super.setPrecision(precision);
        tan.setPrecision(precision);
    }

    @Override
    protected double calculate(double arg) {
        if (Math.abs(arg - Math.PI) < DELTA ) {
            return Double.POSITIVE_INFINITY;
        } else if (Math.abs(arg + Math.PI) < DELTA ) {
            return Double.POSITIVE_INFINITY;
        } else if (Math.abs(arg) < DELTA ) {
            return Double.POSITIVE_INFINITY;
        } else if (Math.abs(arg - Math.PI/2) < DELTA) {
            return 0d;
        } else if (Math.abs(arg + Math.PI/2) < DELTA) {
            return 0d;
        } else if (Math.abs(arg - 2*Math.PI) < DELTA) {
            return Double.POSITIVE_INFINITY;
        } else if (Math.abs(arg + 2*Math.PI) < DELTA) {
            return Double.POSITIVE_INFINITY;
        } else if (Math.abs(arg - 3*Math.PI/2) < DELTA) {
            return 0d;
        } else if (Math.abs(arg + 3*Math.PI/2) < DELTA) {
            return 0d;
        }
        if( isInfinite(arg) || isNaN(arg) ){
            return NaN;
        }
        int scale = 10;
        BigDecimal last;
        BigDecimal value = new BigDecimal(0d, MathContext.UNLIMITED);
        int n = scale;

        double tanVal = tan.calc(arg);
        if( Math.abs(tanVal) < DELTA || isInfinite(tanVal) || isNaN(tanVal) ){
            return NaN;
        }
        do {
            last = value;
            value = new BigDecimal(1d, MathContext.UNLIMITED)
                    .divide(new BigDecimal(tanVal, MathContext.UNLIMITED), n, RoundingMode.HALF_UP);
            n++;
        } while (getPrecision() <= value.subtract(last).abs().doubleValue() && n < MAX_ITERATIONS);
        return value.setScale(n, RoundingMode.UP).doubleValue();
//        return 1 / tan.calc(arg);
    }
}
