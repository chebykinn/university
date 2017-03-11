/**
 * Created by daituganov on 11.03.17.
 */

public class Cosinus {
    public static double cos(double val) {
        double result = 0;
        for(int n = 0; n < 7; n++) {
            result += Math.pow(-1, n) * Math.pow(val, 2 * n) / factorial(2 * n);
        }
        return result;
    }

    private static int factorial(int val) {
        if(val <= 1)
            return 1;
        else
            return val * (factorial(val - 1));
    }
}
