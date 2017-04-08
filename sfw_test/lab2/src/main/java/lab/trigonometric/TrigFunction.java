package lab.trigonometric;

import lab.Calculation;

import static java.lang.Math.pow;

/**
 * Created by ivan on 08.04.17.
 */
public class TrigFunction implements Calculation {
    private double precision;

    public TrigFunction(double precision) {
        this.precision = precision;
    }
    Cosinus cos = new Cosinus(precision);
    Secant sec = new Secant(precision);
    Tangent tan = new Tangent(precision);
    Cotangent cot = new Cotangent(precision);

    @Override
    public double calc(double arg) {
        return ((pow(pow(cot.calc(arg), 2) * sec.calc(arg), 2) - (cot.calc(arg) - cos.calc(arg))) /
                                (tan.calc(arg) / (sec.calc(arg) + tan.calc(arg))));
    }
}
