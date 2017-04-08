package lab.trigonometric;

import lab.AbstractFunction;
import org.junit.Test;

import static lab.AbstractFunction.DELTA;
import static org.junit.Assert.*;
import static java.lang.Math.PI;

/**
 * Created by ivan on 08.04.17.
 */
public class CosinusTest {
    private AbstractFunction fn = new Cosinus(DELTA);
    private String ERROR_FMT = "expected %f == %f +- %f from " + fn.getClass().getSimpleName() + "(%f)\n";
    private void checkPoint(double point){
        for(double precision = 1e-5; precision > 1e-7; precision *= 1e-1) {
            fn.setPrecision(precision);
            fn.setFuncIsStub(true);
            double expected = fn.calc(point);
            fn.setFuncIsStub(false);
            double actual = fn.calc(point);
            assertEquals(String.format(ERROR_FMT, expected, actual, precision, point),
                         expected, actual, precision);
        }
    }

    private void checkRange(double from, double to, double step){
        for(double precision = 1e-5; precision > 1e-7; precision *= 1e-1) {
            fn.setPrecision(precision);
            for(double x = from; x < to; x += step) {
                fn.setFuncIsStub(true);
                double expected = fn.calc(x);
                fn.setFuncIsStub(false);
                double actual = fn.calc(x);
                assertEquals(String.format(ERROR_FMT, expected, actual, precision, x),
                        expected, actual, precision);
            }
        }
    }

    @Test
    public void minusPi() throws Exception {
        checkPoint(-PI);
    }

    @Test
    public void minusPiToMinusHalfPi() throws Exception {
        checkRange(-PI, -PI / 2, PI / 4);
    }

    @Test
    public void minusHalfPi() throws Exception {
        checkPoint(-PI / 2);
    }

    @Test
    public void minusHalfPiToZero() throws Exception {
        checkRange(-PI / 2, 0, PI / 4);
    }

    @Test
    public void zero() throws Exception {
        checkPoint(0);
    }

    @Test
    public void zeroToHalfPi() throws Exception {
        checkRange(0, PI / 2, PI / 4);
    }

    @Test
    public void halfPi() throws Exception {
        checkPoint(PI / 2);
    }

    @Test
    public void halfPiToPi() throws Exception {
        checkRange(PI / 2, PI, PI / 4);
    }

    @Test
    public void pi() throws Exception {
        checkPoint(PI);
    }

    @Test
    public void minusInfinity() throws Exception {
        fn.setFuncIsStub(true);
        double expected = fn.calc(Double.NEGATIVE_INFINITY);
        fn.setFuncIsStub(false);
        double actual = fn.calc(Double.NEGATIVE_INFINITY);
        assertEquals(String.format(ERROR_FMT, expected, actual, DELTA, Double.NEGATIVE_INFINITY),
                expected, actual, DELTA);
    }

    @Test
    public void infinity() throws Exception {
        fn.setFuncIsStub(true);
        double expected = fn.calc(Double.POSITIVE_INFINITY);
        fn.setFuncIsStub(false);
        double actual = fn.calc(Double.POSITIVE_INFINITY);
        assertEquals(String.format(ERROR_FMT, expected, actual, DELTA, Double.POSITIVE_INFINITY),
                expected, actual, DELTA);
    }

    @Test
    public void nan() throws Exception {
        Cosinus fn = new Cosinus(DELTA);
        fn.setFuncIsStub(true);
        double expected = fn.calc(Double.NaN);
        fn.setFuncIsStub(false);
        double actual = fn.calc(Double.NaN);
        assertEquals(String.format(ERROR_FMT, expected, actual, DELTA, Double.NaN),
                expected, actual, DELTA);
    }
}