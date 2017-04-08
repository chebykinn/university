package lab.trigonometric;

import lab.AbstractFunction;
import lab.Functions;
import lab.util.BigDecimalSqrt;

import java.math.BigDecimal;
import java.math.MathContext;

/**
 * Created by ivan on 07.04.17.
 */
public class Tangent extends AbstractFunction {
    {
        table.put(0.0, 0.0);
        function = Functions.TANGENT;
    }

    Cosinus cos;
    double tan;

    public Tangent(Double precision) {
        super(precision);
        cos = new Cosinus(precision);
    }

    @Override
    protected double calculate(double arg) {
        BigDecimal value = new BigDecimal(1d, MathContext.UNLIMITED);

        tan = BigDecimalSqrt.sqrt(value.subtract(new BigDecimal(cos.calc(2 * arg), MathContext.UNLIMITED)).
                divide(new BigDecimal(1d, MathContext.UNLIMITED).add(new BigDecimal(cos.calc(2 * arg)))),
                MathContext.DECIMAL128).doubleValue();

        arg = Cosinus.subOverages(arg);

        if((arg < Math.PI && arg > Math.PI / 2) || (arg > -Math.PI / 2 && arg < 0))
            tan = -tan;
        return tan;
    }
}
