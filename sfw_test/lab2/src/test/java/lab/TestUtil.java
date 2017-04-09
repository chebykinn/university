package lab;

import static org.junit.Assert.assertEquals;

/**
 * Created by ivan on 08.04.17.
 */
public class TestUtil {
    private AbstractFunction fn;
    private String ERROR_FMT = "expected %f == %f +- %f from %s(%f)\n";

    public TestUtil(AbstractFunction fn){
        this.fn = fn;
    }

    public void doCheck(double point, double precision){
        fn.setPrecision(precision);
        fn.setFuncIsStub(true);
        double expected = fn.calc(point);
        fn.setFuncIsStub(false);
        double actual = fn.calc(point);
        assertEquals(String.format(ERROR_FMT, expected, actual, precision, fn.getClass().getSimpleName(), point),
                expected, actual, precision);
    }

    public void checkPoint(double point){
        for(double precision = 1e-5; precision > 1e-7; precision *= 1e-1) {
            doCheck(point, precision);
        }
    }

    public void checkRange(double from, double to, double step){
        for(double precision = 1e-5; precision > 1e-7; precision *= 1e-1) {
            for(double x = from; x < to; x += step) {
                doCheck(x, precision);
            }
        }
    }

}
