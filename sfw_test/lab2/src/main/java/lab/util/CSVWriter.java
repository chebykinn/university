package lab.util;

import lab.AbstractFunction;

import java.io.FileWriter;
import java.io.IOException;
import java.util.Locale;

/**
 * Created by ivan on 08.04.17.
 */
public class CSVWriter {
    public AbstractFunction getFunction() {
        return function;
    }

    private AbstractFunction function;
    public static final String SEPARATOR = ",";


    public CSVWriter(AbstractFunction function){
        this.function = function;
    }

    public void write(double from, double to, double step){
        function.setFuncIsStub(false);
        try (FileWriter writer = new FileWriter(getFilename(), false)) {
            for (double x = from; x < to; x += step) {
                double value = function.calc(x);
                writer.append(String.format(Locale.US, "%f%s%f\n", x, SEPARATOR, value));
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String getFilename(){
        String fnName = this.function.getClass().getSimpleName();
        if( fnName.isEmpty() ){
            fnName = "fn";
        }
        return fnName + "-data.csv";
    }
}
