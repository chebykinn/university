package lab.util;

import lab.AbstractFunction;
import lab.Functions;
import org.junit.Test;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Locale;
import java.util.stream.Stream;

import static org.junit.Assert.*;

/**
 * Created by ivan on 08.04.17.
 */
public class CSVWriterTest {
    @Test
    public void write() throws Exception {
        CSVWriter w = new CSVWriter(new AbstractFunction() {
            {
                table.put(0.0, 0.0);
                function = Functions.STUB;
            }
            @Override
            protected double calculate(double arg) {
                return arg;
            }
        });

        AbstractFunction f = w.getFunction();

        double from = 0.0;
        double to = 5.0;
        double step = 0.1;
        final double[] x = {from};
        w.write(from, to, step);

        try (Stream<String> stream = Files.lines(Paths.get(w.getFilename()))) {
            stream.forEach((s -> {
                assertEquals(s,String.format(Locale.US, "%f%s%f", x[0], w.SEPARATOR, f.calc(x[0])));
                x[0] += step;
            }));
        }
    }

}