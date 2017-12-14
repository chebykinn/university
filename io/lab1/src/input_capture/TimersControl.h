#pragma once

#include <systemc.h>

SC_MODULE(TimersControl) {
    sc_in<sc_uint<32>>  timer1_i;
    sc_in<sc_uint<32>>  timer2_i;
    sc_in<sc_uint<2>>   ictmr_i;
    sc_out<sc_uint<32>> tval_o;


    SC_HAS_PROCESS(TimersControl);

    TimersControl(sc_module_name nm);
    ~TimersControl() = default;

    private:
    void on_change();
};
