package lab.logarithmic;

import lab.AbstractFunction;
import lab.Functions;

/**
 * Created by ivan on 08.04.17.
 */
public class LogN extends AbstractFunction {
    private static final int DEFAULT_BASE = 2;

    private final int base;
    private Ln ln;

    {
        table.put(0.0, 0.0);
        function = Functions.LOG_N;
    }

    public LogN() {
        super();
        this.base = DEFAULT_BASE;
    }

    public LogN(boolean isStub, int base, double precision) {
        super(precision);
        if (base < 0 || base == 1) {
            throw new IllegalArgumentException();
        }
        this.base = base;
        ln = new Ln(precision);
    }

    @Override
    protected double calculate(double arg) {

        if (Math.abs(arg - base) < DELTA) {
            return 1d;
        }

        if (Math.abs(arg - 1d) < DELTA) {
            return 0d;
        }

        return ln.calc(arg) / ln.calc(base);
    }
}
