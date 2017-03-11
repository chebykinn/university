import org.junit.runner.JUnitCore;
import org.junit.runner.Result;
import org.junit.runner.notification.Failure;

/**
 * Created by daituganov on 12.03.17.
 */
public class TestRunner {
    public static void main(String[] args) {
        Result result = JUnitCore.runClasses(CosinusTest.class);

        for (Failure failure : result.getFailures()) {
            System.out.println(failure.toString());
        }

        System.out.println(result.wasSuccessful());
    }
}
