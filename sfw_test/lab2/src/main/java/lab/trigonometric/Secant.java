package lab.trigonometric;

import lab.AbstractFunction;
import lab.Functions;

import static java.lang.Math.PI;

/**
 * Created by ivan on 07.04.17.
 */
public class Secant extends AbstractFunction{
    {
        table.put(-PI, -1.0);
        table.put(-PI / 2, Double.POSITIVE_INFINITY);
        table.put(0.0, 1.0);
        table.put(PI / 2, Double.POSITIVE_INFINITY);
        table.put(PI, -1.0);

        table.put(3 * PI / 4, -1.4142135627461);
        table.put(-3 * PI / 4, -1.4142135627461);
        table.put( PI / 4, 1.4142135627461);
        table.put(-PI / 4, 1.4142135627461);
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
