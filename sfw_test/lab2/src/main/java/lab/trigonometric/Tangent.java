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

        table.put(-PI * 1.5, NaN);
        table.put(-PI * 1.5 + PI / 16, -5.027339481844);
        table.put(-PI * 1.5 + 2 * PI / 16, -2.414214618296);
    }

    Cosinus cos;
    double tan;

    public Tangent(double precision) {
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
        if (Math.abs(arg - Math.PI) < getPrecision() ) {
            return 0d;
        } else if (Math.abs(arg + Math.PI) < getPrecision() ) {
            return 0d;
        } else if (Math.abs(arg) < getPrecision() ) {
            return 0d;
        } else if (Math.abs(arg - Math.PI/2) < getPrecision()) {
            return NaN;
        } else if (Math.abs(arg + Math.PI/2) < getPrecision()) {
            return NaN;
        } else if (Math.abs(arg - 2*Math.PI) < getPrecision()) {
            return 0d;
        } else if (Math.abs(arg + 2*Math.PI) < getPrecision()) {
            return 0d;
        } else if (Math.abs(arg - 3*Math.PI/2) < getPrecision()) {
            return NaN;
        } else if (Math.abs(arg + 3*Math.PI/2) < getPrecision()) {
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
            try {
                value = BigDecimalSqrt.sqrt(
                    new BigDecimal(1d, MathContext.UNLIMITED)
                            .divide(new BigDecimal(cosVal*cosVal, MathContext.UNLIMITED), n, RoundingMode.UP)
                            .subtract(new BigDecimal(1d, MathContext.UNLIMITED)),
                    MathContext.DECIMAL128
                );
            }catch (ArithmeticException e) {
                return NaN;
            }
            n++;
        } while (getPrecision() <= value.subtract(last).abs().doubleValue() && n < MAX_ITERATIONS);

        tan = value.setScale(n, RoundingMode.UP).doubleValue();
//        tan = sqrt(1 / pow(cosVal, 2) - 1);
        arg = subOverages(arg);

        if(arg > -PI / 2 && arg < 0 || arg > PI / 2 && arg < PI)
            tan = -tan;
        return tan;
    }

    protected static double subOverages(double arg) {
        long periodCounter = (long) (arg / PI) + ((arg > 0)? 1: -1);

        if(arg > PI / 2 || arg < -PI / 2)
            arg -= periodCounter * PI;
        return arg;
    }
}
