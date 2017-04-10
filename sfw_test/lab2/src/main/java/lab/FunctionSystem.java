package lab;

import lab.logarithmic.LogFunction;
import lab.trigonometric.TrigFunction;

/**
 * Created by ivan on 07.04.17.
 */
public class FunctionSystem extends AbstractFunction{
    private Double precision;
    private TrigFunction trigFunction;
    private LogFunction logFunction;

    {
        table.put(0.01, 7.032062786082151);
        table.put(0.0, 1.0);
        table.put(-0.01, -9.899639988666914E9);

        table.put(1.01, 7.032062786082151);
        table.put(1.0, 1.0);
        table.put(0.99, -9.899639988666914E9);
        function = Functions.SYS_FN;
    }

    FunctionSystem(Double precision) {
        super(precision);
        this.precision = precision;
        trigFunction = new TrigFunction(precision);
        logFunction = new LogFunction(precision);
    }

    @Override
    public double calculate(double arg){
        if(arg <= 0)
            return trigFunction.calc(arg);
        else
            return logFunction.calc(arg);
    }
}
