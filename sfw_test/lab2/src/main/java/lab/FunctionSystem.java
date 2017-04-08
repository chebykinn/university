package lab;

import lab.AbstractFunction;

/**
 * Created by ivan on 07.04.17.
 */
public class FunctionSystem extends AbstractFunction {
    public FunctionSystem(boolean isStub, double precision) {
        super(isStub, precision);
    }

    @Override
    public double calc(double value){
        // TODO: add stub then function
        // if x <= 0 -> trigonometric function
        // else -> logarithmic function
        return 0;
    }

    @Override
    protected double calculate(double arg) {
        return 0;
    }
}
