#pragma once

#include "systemc.h"

SC_MODULE(Signal)
{
    sc_out<bool> data_o;

    SC_HAS_PROCESS(Signal);

    Signal(sc_module_name nm);
    ~Signal() = default;

private:
    void run();
};
