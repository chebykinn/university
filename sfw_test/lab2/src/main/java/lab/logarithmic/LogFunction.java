package lab.logarithmic;

import lab.AbstractFunction;
import lab.Calculation;
import lab.Functions;
import lab.trigonometric.TrigFunction;

import static java.lang.Math.pow;

/**
 * Created by daituganov on 08.04.17.
 */
public class LogFunction extends AbstractFunction {
    private double precision;
    Ln ln;
    Log2 log2;
    Log10 log10;

    {

        table.put(0.01, 7.032062786082151);
        table.put(0.99, 1.6060455474124632e-10);
        table.put(1.01, 1.5430914352620607e-10);
        function = Functions.LOG_FN;
    }

    public LogFunction(double precision) {
        super(precision);
        this.precision = precision;
        ln = new Ln(precision);
        log2 = new Log2(precision);
        log10 = new Log10(precision);
    }

    @Override
    public void setPrecision(double precision){
        super.setPrecision(precision);
        ln.setPrecision(precision);
        log2.setPrecision(precision);
        log10.setPrecision(precision);
    }

    @Override
    public double calculate(double arg) {
        return (pow(pow(ln.calc(arg) - log10.calc(arg), 2) / log2.calc(arg), 3) * log2.calc(arg));
    }
}
