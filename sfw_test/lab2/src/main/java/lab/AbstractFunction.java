package lab;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by ivan on 08.04.17.
 */
public abstract class AbstractFunction implements Calculation {

    private static final double DEFAULT_PRECISION = 0.000001;
    public static final double DELTA = 1e-4;
    public static final int MAX_ITERATIONS = 1_000_000;
    public final Calculation DEFAULT_IMPLEMENTATION = this::calculate;

    private double precision;

    private Calculation calculation;
    protected Map<Double, Double> table = new HashMap<>();

    public AbstractFunction() {
        this.precision = DEFAULT_PRECISION;
        calculation = DEFAULT_IMPLEMENTATION;
    }

    protected AbstractFunction(boolean isStub, Double precision) {
        if (precision == null) {
            this.precision = DEFAULT_PRECISION;
        } else {
            this.precision = precision.doubleValue();
        }

        if (isStub) {
            calculation = this::stub;
        } else {
            calculation = this::calculate;
        }
    }

    public double calc(double arg) {
        return calculation.calc(arg);
    }

    public double getPrecision() {
        return precision;
    }

    protected double stub(double arg) {
        Double result;

        if((result = table.get(arg)) != null)
            return result.doubleValue();
        else
            return Double.NaN;
    }

    protected abstract double calculate(double arg);
}
