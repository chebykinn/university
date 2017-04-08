package lab;

import lab.logarithmic.LogFunction;
import lab.trigonometric.TrigFunction;

/**
 * Created by ivan on 07.04.17.
 */
public class FunctionSystem implements Calculation{
    private double precision;

    FunctionSystem(double precision) {
        this.precision = precision;
    }

    private TrigFunction trigFunction = new TrigFunction(precision);
    private LogFunction logFunction = new LogFunction(precision);

    @Override
    public double calc(double arg){
        if(arg <= 0)
            return trigFunction.calc(arg);
        else
            return logFunction.calc(arg);
    }
}
