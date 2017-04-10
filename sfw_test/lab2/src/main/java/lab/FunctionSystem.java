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
        table.put(0.0, Double.NaN);
        table.put(-0.01, -9.899637632677807E9);

        table.put(1.01, 1.5429930377249587E-10);
        table.put(1.0, Double.NaN);
        table.put(0.99, 1.6059643134973574E-10);
        function = Functions.SYS_FN;
    }

    FunctionSystem(Double precision) {
        super(precision);
        this.precision = precision;
        trigFunction = new TrigFunction(precision);
        logFunction = new LogFunction(precision);
    }

    @Override
    public void setPrecision(double precision){
        super.setPrecision(precision);
        trigFunction.setPrecision(precision);
        logFunction.setPrecision(precision);
    }

    @Override
    public double calculate(double arg){
        if(arg <= 0)
            return trigFunction.calc(arg);
        else
            return logFunction.calc(arg);
    }
}
