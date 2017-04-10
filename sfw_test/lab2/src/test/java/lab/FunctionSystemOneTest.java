package lab;

import lab.logarithmic.Ln;
import lab.logarithmic.Log10;
import lab.logarithmic.Log2;
import lab.logarithmic.LogFunction;
import lab.trigonometric.*;
import lab.util.CSVWriter;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import static lab.AbstractFunction.DELTA;

/**
 * Created by ivan on 08.04.17.
 */
public class FunctionSystemOneTest {
    private double precision = DELTA;
    private TestUtil util = new TestUtil(new FunctionSystem(precision));

    @Before
    public void setUp(){
        Cosinus cos = new Cosinus(precision);
        cos.setFuncIsStub(false);
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