package lab.logarithmic;

import lab.TestUtil;
import org.junit.Test;

import static lab.AbstractFunction.DELTA;
import static org.junit.Assert.*;

/**
 * Created by ivan on 09.04.17.
 */
public class Log2Test {
    private final double base = 2;
    private double precision = DELTA;
    private TestUtil util = new TestUtil(new Log2(precision));

    @Test
    public void negativeInfinity() {
        util.doCheck(Double.NEGATIVE_INFINITY, precision);
    }

    @Test
    public void positiveInfinity() {
        util.doCheck(Double.POSITIVE_INFINITY, precision);
    }

    @Test
    public void nan() {
        util.doCheck(Double.NaN, precision);
    }

    @Test
    public void ltZero() {
        util.doCheck(-0.01, precision);
    }

    @Test
    public void zero() {
        util.doCheck(0.0, precision);
    }

    @Test
    public void gtZero() {
        util.doCheck(0.01, precision);
    }

    @Test
    public void ltOne() {
        util.doCheck(1 - 0.01, precision);
    }

    @Test
    public void one() {
        util.doCheck(1.0, precision);
    }

    @Test
    public void gtOne() {
        util.doCheck(1.0 + 0.01, precision);
    }

    @Test
    public void ltBase() {
        util.doCheck(base - 0.01, precision);
    }

    @Test
    public void base() {
        util.doCheck(base, precision);
    }

    @Test
    public void gtBase() {
        util.doCheck(base + 0.01, precision);
    }

}