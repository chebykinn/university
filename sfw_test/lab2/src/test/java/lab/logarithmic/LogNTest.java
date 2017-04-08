package lab.logarithmic;

import org.junit.Test;

import static lab.AbstractFunction.DELTA;
import static org.junit.Assert.*;

/**
 * Created by ivan on 08.04.17.
 */
public class LogNTest {
    @Test
    public void zeroEq(){
        Log2 l = new Log2(0.1);
        double value = 0;
        double actual = l.calc(value);
        double expected = 0;
        assertEquals(expected, actual, DELTA);
    }

    @Test
    public void zeroLt(){
        Log2 l = new Log2(0.1);
        double value = 0;
        double actual = l.calc(value);
        double expected = 0;
        assertEquals(expected, actual, DELTA);
    }

    @Test
    public void zeroGt(){
        Log2 l = new Log2(0.1);
        double value = 0;
        double actual = l.calc(value);
        double expected = 0;
        assertEquals(expected, actual, DELTA);

    }

    @Test
    public void oneEq(){
        Log2 l = new Log2(0.1);
        double value = 0;
        double actual = l.calc(value);
        double expected = 0;
        assertEquals(expected, actual, DELTA);
    }

    @Test
    public void oneLt(){
        Log2 l = new Log2(0.1);
        double value = 0;
        double actual = l.calc(value);
        double expected = 0;
        assertEquals(expected, actual, DELTA);
    }

    @Test
    public void oneGt(){
        Log2 l = new Log2(0.1);
        double value = 0;
        double actual = l.calc(value);
        double expected = 0;
        assertEquals(expected, actual, DELTA);
    }

    @Test
    public void baseEq(){
        Log2 l = new Log2(0.1);
        double value = 0;
        double actual = l.calc(value);
        double expected = 0;
        assertEquals(expected, actual, DELTA);
    }

    @Test
    public void baseLt(){
        Log2 l = new Log2(0.1);
        double value = 0;
        double actual = l.calc(value);
        double expected = 0;
        assertEquals(expected, actual, DELTA);
    }

    @Test
    public void baseGt(){
        Log2 l = new Log2(0.1);
        double value = 0;
        double actual = l.calc(value);
        double expected = 0;
        assertEquals(expected, actual, DELTA);
    }

    @Test
    public void negativeInfinity() throws Exception {
        Log2 l = new Log2(0.1);
        double value = 0;
        double actual = l.calc(value);
        double expected = 0;
        assertEquals(expected, actual, DELTA);
    }

    @Test
    public void positiveInfinity() throws Exception {
        Log2 l = new Log2(0.1);
        double value = 0;
        double actual = l.calc(value);
        double expected = 0;
        assertEquals(expected, actual, DELTA);
    }

}