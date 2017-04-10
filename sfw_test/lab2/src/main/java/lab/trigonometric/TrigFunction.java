package lab.trigonometric;

import lab.AbstractFunction;
import lab.Calculation;
import lab.Functions;

import static java.lang.Double.NaN;
import static java.lang.Math.pow;

/**
 * Created by ivan on 08.04.17.
 */
public class TrigFunction extends AbstractFunction {
    private double precision;
    Cosinus cos;
    Secant sec;
    Tangent tan;
    Cotangent cot;

    {
        table.put(0.01, 1.0099609991042002e10);
        table.put(-0.01, -9.899639988666914e9);
        table.put(0.0, NaN);

        table.put(1.01, 0.9898052287352547);
        table.put(1.0, 1.0514533523222378);
        table.put(0.99, 1.1170816671751458);

        table.put(1.5708 + 0.01, 2.001475909139914e-4);
        table.put(1.5708, 0.0);
        table.put(1.5708 - 0.01, 1.9888998093426087e-4);

        table.put(-17.278800 + 0.01, 2.001475909139914e-4);
        table.put(-17.278800, 0.0);
        table.put(-17.278800 - 0.01, 1.9888998093426087e-4);
        function = Functions.TRIG_FN;
    }

    public TrigFunction(double precision) {
        super(precision);
        this.precision = precision;
        cos = new Cosinus(precision);
        sec = new Secant(precision);
        tan = new Tangent(precision);
        cot = new Cotangent(precision);
    }

    @Override
    public double calculate(double arg) {
//        if( Math.abs(arg) < DELTA ){
//            return NaN;
//        }
        double cosVal = cos.calc(arg);
        double secVal = sec.calc(arg);
        double tanVal = tan.calc(arg);
        double cotVal = cot.calc(arg);
        return ((pow(pow(cotVal, 2) * secVal, 2) - (cotVal - cosVal)) /
                                (tanVal / (secVal + tanVal)));
    }
}
