package lab;

import lab.logarithmic.Ln;
import lab.logarithmic.Log2;
import lab.trigonometric.*;
import lab.util.CSVWriter;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import static lab.AbstractFunction.DELTA;

/**
 * Created by ivan on 08.04.17.
 */
public class FunctionSystemSevenTest {
    private double precision = DELTA;
    private TestUtil util = new TestUtil(new FunctionSystem(precision));

    @Before
    public void setUp(){
        Cosinus cos = new Cosinus(precision);
        cos.setFuncIsStub(false);
        Secant sec = new Secant(precision);
        sec.setFuncIsStub(false);
        Tangent tan = new Tangent(precision);
        tan.setFuncIsStub(false);
        Cotangent cot = new Cotangent(precision);
        cot.setFuncIsStub(false);
        TrigFunction tFn = new TrigFunction(precision);
        tFn.setFuncIsStub(false);

        Ln ln = new Ln(DELTA);
        ln.setFuncIsStub(false);
        Log2 log2 = new Log2(DELTA);
        log2.setFuncIsStub(false);
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

    @Test(timeout = 10000)
    @Ignore
    public void save(){
        CSVWriter w = new CSVWriter(new FunctionSystem(1e-3));
//        w.write(0, 10, DELTA);
//        w.setAppend(true);
//        w.getFunction().setPrecision(DELTA);
//        w.write(-6.0, -3.2, 1e-3);
//        w.setAppend(true);

//        w.write(-4.0, -PI, 1e-3);

//        w.write(-3.2, -2.0, 1e-3);
//        w.setAppend(true);

//        w.write(-2.0, -1.0, 1e-3);

//        w.write(-1.0, 0.0, 1e-3);
    }
}