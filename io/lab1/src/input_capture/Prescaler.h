#pragma once

#include <systemc.h>

SC_MODULE(Prescaler) {
    sc_in<bool>         clk_i;
    sc_in<sc_uint<3>>   icm_i;
    sc_in<bool>         ins_i;
    sc_out<bool>        ins_o;

    SC_HAS_PROCESS(Prescaler);

    Prescaler(sc_module_name nm);
    ~Prescaler() = default;

    private:
    sc_uint<5>          reg_counter;

    void on_clk();
    int get_counter_threshold();
};
