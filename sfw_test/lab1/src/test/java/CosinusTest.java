import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by daituganov on 11.03.17.
 */
public class CosinusTest extends Assert {
    private HashMap<Double, Double> arrayTestValues = new HashMap<Double, Double>();
    private static final double DELTA = 0.001;

    @Before
    public void setUp() {
        //hashMap, key - table result, value - input value
        arrayTestValues.put((double) 1, (double) 0);
        arrayTestValues.put(Math.sqrt(3) / 2, Math.PI / 6);
        arrayTestValues.put(Math.sqrt(2) / 2, Math.PI / 4);
        arrayTestValues.put(0.5, Math.PI / 3);
        arrayTestValues.put((double) 0, Math.PI / 2);
        arrayTestValues.put(-0.5, 2 * Math.PI / 3);
        arrayTestValues.put(-Math.sqrt(2) / 2, 3 * Math.PI / 4);
        arrayTestValues.put(-Math.sqrt(3) / 2, 5 * Math.PI / 6);
        arrayTestValues.put((double) -1, Math.PI);
    }

    @After
    public void tearDown() {
        arrayTestValues.clear();
    }

    @Test
    public void testCos() {
        for(Map.Entry entry : arrayTestValues.entrySet()) {
            double expected = ( (Double) entry.getKey()).doubleValue();
            double actual = Cosinus.cos( ( (Double) entry.getValue() ).doubleValue());
            assertEquals(expected, actual, DELTA);
        }
    }
}
