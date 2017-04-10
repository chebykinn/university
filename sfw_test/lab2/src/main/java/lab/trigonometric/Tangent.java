package lab.trigonometric;

import lab.AbstractFunction;
import lab.Functions;
import lab.util.BigDecimalSqrt;
import lab.util.FactorialSeries;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;

import static java.lang.Double.*;
import static java.lang.Math.PI;
import static java.lang.Math.pow;
import static java.lang.Math.sqrt;

/**
 * Created by ivan on 07.04.17.
 */
public class Tangent extends AbstractFunction {

    {
        table.put(-PI, 0.0);
        table.put(-PI / 2, NaN);
        table.put(0.0, 0.0);
        table.put(PI / 2, NaN);
        table.put(PI, 0.0);

//        table.put(3 * PI / 4, -1.0000051536258532);
//        table.put(-3 * PI / 4, 1.0000051536258532);
        table.put(3 * PI / 4, -1.0);
        table.put(-3 * PI / 4, 1.0);
        table.put( PI / 4, 1.0);
        table.put(-PI / 4, -1.0);
        function = Functions.TANGENT;
    }

    Cosinus cos;
    double tan;

    public Tangent(double precision) {
        super(precision);
        cos = new Cosinus(precision);
    }

    @Override
    protected double calculate(double arg) {
        if (Math.abs(arg - Math.PI) < DELTA ) {
            return 0d;
        } else if (Math.abs(arg + Math.PI) < DELTA ) {
            return 0d;
        } else if (Math.abs(arg) < DELTA ) {
            return 0d;
        } else if (Math.abs(arg - Math.PI/2) < DELTA) {
            return NaN;
        } else if (Math.abs(arg + Math.PI/2) < DELTA) {
            return NaN;
        } else if (Math.abs(arg - 2*Math.PI) < DELTA) {
            return 0d;
        } else if (Math.abs(arg + 2*Math.PI) < DELTA) {
            return 0d;
        } else if (Math.abs(arg - 3*Math.PI/2) < DELTA) {
            return NaN;
        } else if (Math.abs(arg + 3*Math.PI/2) < DELTA) {
            return NaN;
        }

        if( isInfinite(arg) || isNaN(arg) ){
            return NaN;
        }

        double cosVal = cos.calc(arg);
        int scale = 10;
        BigDecimal last;
        BigDecimal value = new BigDecimal(0d, MathContext.UNLIMITED);
        int n = scale;

        do {
            last = value;
            value = BigDecimalSqrt.sqrt(
                new BigDecimal(1d, MathContext.UNLIMITED)
                        .divide(new BigDecimal(cosVal*cosVal, MathContext.UNLIMITED), n, RoundingMode.HALF_UP)
                        .subtract(new BigDecimal(1d, MathContext.UNLIMITED)),
                MathContext.DECIMAL128
            );
            n++;
        } while (getPrecision() <= value.subtract(last).abs().doubleValue() && n < MAX_ITERATIONS);

        tan = value.setScale(n, RoundingMode.UP).doubleValue();
//        tan = sqrt(1 / pow(cosVal, 2) - 1);
        arg = Cosinus.subOverages(arg);

        if((arg < PI && arg > PI / 2) || (arg > -PI / 2 && arg < 0))
            tan = -tan;
        return tan;
    }
}
