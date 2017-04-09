package lab.trigonometric;

import lab.AbstractFunction;
import lab.Functions;
import lab.util.BigDecimalSqrt;

import java.math.BigDecimal;
import java.math.MathContext;

import static java.lang.Double.*;
import static java.lang.Math.PI;

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

        BigDecimal value = new BigDecimal(1d, MathContext.UNLIMITED);
        double cosVal = cos.calc(2 * arg);
        try {
            tan = BigDecimalSqrt.sqrt(value.subtract(new BigDecimal(cosVal, MathContext.UNLIMITED)).
                            divide(new BigDecimal(1d, MathContext.UNLIMITED).add(new BigDecimal(cosVal))),
                    MathContext.DECIMAL128).doubleValue();
        }catch (Exception e) {
            tan = NaN;
        }

        arg = Cosinus.subOverages(arg);

        if((arg < PI && arg > PI / 2) || (arg > -PI / 2 && arg < 0))
            tan = -tan;
        return tan;
    }
}
