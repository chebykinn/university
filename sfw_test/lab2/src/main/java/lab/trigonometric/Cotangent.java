package lab.trigonometric;

import lab.AbstractFunction;
import lab.Functions;

import static java.lang.Math.PI;

/**
 * Created by ivan on 07.04.17.
 */
public class Cotangent extends AbstractFunction {
    {
        table.put(-PI, Double.NaN);
        table.put(-PI / 2, 0.0);
        table.put(0.0, Double.NaN);
        table.put(PI / 2, 0.0);
        table.put(PI, Double.NaN);

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
    protected double calculate(double arg) {
        return 1 / tan.calc(arg);
    }
}
