package lab.trigonometric;

import lab.AbstractFunction;

/**
 * Created by ivan on 07.04.17.
 */
public class Cotangent extends AbstractFunction {
    Cosinus cos;

    public Cotangent(boolean isStub, double precision) {
        super(isStub, precision);
        cos = new Cosinus(isStub, precision);
    }


    @Override
    protected double calculate(double arg) {

        return 0;
    }
}
