package lab.trigonometric;

import lab.AbstractFunction;
import lab.Functions;

/**
 * Created by ivan on 07.04.17.
 */
public class Cotangent extends AbstractFunction {
    {
        table.put(0.0, 0.0);
        function = Functions.COTANGENT;
    }

    Tangent tan;

    public Cotangent(Double precision) {
        super(precision);
        tan = new Tangent(precision);
    }


    @Override
    protected double calculate(double arg) {
        return 1 / tan.calc(arg);
    }
}
