package lab.trigonometric;

import lab.AbstractFunction;

import java.math.BigDecimal;
import java.math.MathContext;

/**
 * Created by ivan on 07.04.17.
 */
public class Tangent extends AbstractFunction {
    Cosinus cos;

    public Tangent(boolean isStub, double precision) {
        super(isStub, precision);
        cos = new Cosinus(isStub, precision);
    }

    @Override
    protected double calculate(double arg) {
        BigDecimal value = new BigDecimal(0d, MathContext.UNLIMITED);


        return 0;
    }
}
