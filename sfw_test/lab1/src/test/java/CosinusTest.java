import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;

/**
 * Created by daituganov on 11.03.17.
 */
public class CosinusTest extends Assert {
    private HashMap<Double, Double> arrayTestValues = new HashMap<>();
    private HashMap<Double, Double> arrayWrongTestValues = new HashMap<>();
    private static final double DELTA = 0.001;

    @Before
    public void setUp() {
        //hashMap, key - table result, value - input value
        //zero check
        arrayTestValues.put((double) 1, (double) 0);

        //right side check
        arrayTestValues.put(Math.sqrt(3) / 2, Math.PI / 6);
        arrayTestValues.put((double) 0, Math.PI / 2);
        arrayTestValues.put(-Math.sqrt(2) / 2, 3 * Math.PI / 4);

        //parity check
        arrayTestValues.put(Math.sqrt(3) / 2, -Math.PI / 6);

        //test coverage boundaries
        arrayTestValues.put((double) -1, -Math.PI);
        arrayTestValues.put((double) -1, Math.PI);

        //test NaN, Infinity
        arrayTestValues.put(Double.NaN, Double.NaN);
        arrayTestValues.put(Double.NaN, Double.POSITIVE_INFINITY);

        //test values outside the coverage boundaries
        arrayWrongTestValues.put(Math.sqrt(3) / 2, 7 * Math.PI / 6);
        arrayWrongTestValues.put(Math.sqrt(3) / 2, -7 * Math.PI / 6);
    }

    @After
    public void tearDown() {
        arrayTestValues.clear();
    }

    @Test
    public void testCos() {
        double expected, actual;
        for(Map.Entry entry : arrayTestValues.entrySet()) {
            expected = ( (Double) entry.getKey()).doubleValue();
            actual = Cosinus.cos( ( (Double) entry.getValue() ).doubleValue());
            assertEquals(expected, actual, DELTA);
        }
        for(Map.Entry entry : arrayWrongTestValues.entrySet()) {
            expected = ( (Double) entry.getKey()).doubleValue();
            actual = Cosinus.cos( ( (Double) entry.getValue() ).doubleValue());
            assertNotEquals(expected, actual, DELTA);
        }
    }
}
