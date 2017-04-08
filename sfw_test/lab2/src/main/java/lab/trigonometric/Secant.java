package lab.trigonometric;

import lab.AbstractFunction;
import lab.Functions;

/**
 * Created by ivan on 07.04.17.
 */
public class Secant extends AbstractFunction{
    {
        table.put(0.0, 0.0);
        function = Functions.SECANT;
    }

    Cosinus cos;

    public Secant(double precision) {
        super(precision);
        cos = new Cosinus(precision);
    }

    @Override
    protected double calculate(double arg) {
        return 1 / cos.calc(arg);
    }
}
