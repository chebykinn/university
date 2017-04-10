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

import static java.lang.Math.PI;
import static lab.AbstractFunction.DELTA;
import static org.junit.Assert.*;

/**
 * Created by ivan on 08.04.17.
 */
public class FunctionSystemTest {
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
        Log10 log10 = new Log10(DELTA);
        log10.setFuncIsStub(false);

        LogFunction lFn = new LogFunction(precision);
        lFn.setFuncIsStub(false);
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
        CSVWriter w = new CSVWriter(new FunctionSystem(precision));
        w.write(0, 100, DELTA);
        w.setAppend(true);
        w.write(-PI, 0, DELTA);
    }
}