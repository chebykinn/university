package lab.trigonometric;

import lab.TestUtil;
import org.junit.Test;

import static java.lang.Math.PI;
import static lab.AbstractFunction.DELTA;
import static org.junit.Assert.*;

/**
 * Created by ivan on 08.04.17.
 */
public class CotangentTest {
    TestUtil util = new TestUtil(new Cotangent(DELTA));
    @Test
    public void minusPi() throws Exception {
        util.checkPoint(-PI);
    }

    @Test
    public void minusPiToMinusHalfPi() throws Exception {
        util.checkRange(-PI, -PI / 2, PI / 4);
    }

    @Test
    public void minusHalfPi() throws Exception {
        util.checkPoint(-PI / 2);
    }

    @Test
    public void minusHalfPiToZero() throws Exception {
        util.checkRange(-PI / 2, 0, PI / 4);
    }

    @Test
    public void zero() throws Exception {
        util.checkPoint(0);
    }

    @Test
    public void zeroToHalfPi() throws Exception {
        util.checkRange(0, PI / 2, PI / 4);
    }

    @Test
    public void halfPi() throws Exception {
        util.checkPoint(PI / 2);
    }

    @Test
    public void halfPiToPi() throws Exception {
        util.checkRange(PI / 2, PI, PI / 4);
    }

    @Test
    public void pi() throws Exception {
        util.checkPoint(PI);
    }

    @Test
    public void minusInfinity() throws Exception {
        util.doCheck(Double.NEGATIVE_INFINITY, DELTA);
    }

    @Test
    public void infinity() throws Exception {
        util.doCheck(Double.POSITIVE_INFINITY, DELTA);
    }

    @Test
    public void nan() throws Exception {
        util.doCheck(Double.NaN, DELTA);
    }
}