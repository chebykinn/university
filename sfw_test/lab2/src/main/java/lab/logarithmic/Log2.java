package lab.logarithmic;

import lab.Functions;

/**
 * Created by daituganov on 08.04.17.
 */
public class Log2 extends LogN {
    public static final int BASE = 2;

    {
        table.put(0.0, 0.0);
        function = Functions.LOG_2;
    }

    public Log2(Double precision) {
        super(precision);
    }

    @Override
    protected double calculate(double arg) {
        return log(arg, BASE);
    }
}
