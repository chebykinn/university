package lab.logarithmic;

import lab.Functions;

/**
 * Created by daituganov on 08.04.17.
 */
public class Log10 extends LogN {
    public static final int BASE = 10;

    {
        table.put(Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY);
        table.put(10.0 - 0.01, 0.999565);
        table.put(10.0, 1.0);
        table.put(10.0 + 0.01, 1.00043);

        table.put(0.0 - 0.01, Double.NaN);
        table.put(0.0, Double.NEGATIVE_INFINITY);
        table.put(0.0 + 0.01, -1.9966406951372424);

        table.put(1.0 - 0.01, -0.00436481);
        table.put(1.0, 0.0);
        table.put(1.0 + 0.01, 0.00432137);
        function = Functions.LOG_10;
    }

    public Log10(double precision) {
        super(precision);
    }

    @Override
    protected double calculate(double arg) {
        return log(arg, BASE);
    }
}
