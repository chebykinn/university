#include "Signal.h"

Signal::Signal(sc_module_name nm)
        : sc_module(nm),
          data_o("data_o")
{
    data_o.initialize(0);
    SC_THREAD(run);
}


void Signal::run() {
    wait(100, SC_NS);
    for (int i = 80; i < 666; i += 10) {
        data_o.write(1);
        wait(i, SC_NS);
        data_o.write(0);
        wait(10, SC_NS);
    }
}
