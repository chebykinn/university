package lab.trigonometric;

import lab.TestUtil;
import org.junit.Before;
import org.junit.Test;

import static lab.AbstractFunction.DELTA;
import static org.junit.Assert.*;

/**
 * Created by ivan on 08.04.17.
 */
public class FunctionTest {
    private double precision = DELTA;
    private TestUtil util = new TestUtil(new TrigFunction(precision));

    @Before
    public void setUp() throws Exception {
        Cosinus cos = new Cosinus(precision);
        cos.setFuncIsStub(false);
        Secant sec = new Secant(precision);
        sec.setFuncIsStub(false);
        Tangent tan = new Tangent(precision);
        tan.setFuncIsStub(false);
        Cotangent cot = new Cotangent(precision);
        cot.setFuncIsStub(false);
    }

    @Test
    public void negativeInfinity(){
        util.doCheck(Double.NEGATIVE_INFINITY, precision);
    }

    @Test
    public void positiveInfinity(){
        util.doCheck(Double.POSITIVE_INFINITY, precision);
    }

    @Test
    public void nan(){
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
    public void gtFirstExtrema() {
        double extrema = 1.5708 + 0.01;
        util.doCheck(extrema, precision);
    }

    @Test
    public void firstExtrema() {
        double extrema = 1.5708;
        util.doCheck(extrema, precision);
    }

    @Test
    public void ltFirstExtrema() {
        double extrema = 1.5708 - 0.01;
        util.doCheck(extrema, precision);
    }

    @Test
    public void gtSecondExtrema() {
        double extrema = -17.2788 + 0.01;
        util.doCheck(extrema, precision);
    }

    @Test
    public void secondExtrema() {
        double extrema = -17.2788;
        util.doCheck(extrema, precision);
    }

    @Test
    public void ltSecondExtrema() {
        double extrema = -17.2788 - 0.01;
        util.doCheck(extrema, precision);
    }

    @Test
    public void gtThirdExtrema() {
        double extrema = -10.9956 + 0.01;
        util.doCheck(extrema, precision);
    }

    @Test
    public void thirdExtrema() {
        double extrema = -10.9956;
        util.doCheck(extrema, precision);
    }

    @Test
    public void ltThirdExtrema() {
        double extrema = -10.9956 - 0.01;
        util.doCheck(extrema, precision);
    }
}
