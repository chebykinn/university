package lab.logarithmic;

import lab.Functions;

/**
 * Created by daituganov on 08.04.17.
 */
public class Log10 extends LogN {
    public static final int BASE = 10;

    {
        table.put(0.0, 0.0);
        function = Functions.LOG_10;
    }

    public Log10(Double precision) {
        super(precision);
    }

    @Override
    protected double calculate(double arg) {
        return log(arg, BASE);
    }
}
