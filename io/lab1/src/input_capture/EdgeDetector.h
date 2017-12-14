#pragma once

#include <systemc.h>

SC_MODULE(EdgeDetector) {
    enum ed_icm_mode {
        Any,
        Pos,
        Neg
    };

    sc_in<bool>         clk_i;
    sc_in<sc_uint<3>>   icm_i;
    sc_in<bool>         ins_i;
    sc_out<bool>        ins_o;

    SC_HAS_PROCESS(EdgeDetector);

    EdgeDetector(sc_module_name nm);
    ~EdgeDetector() = default;

    private:
    bool reg_prev_ins;

    void on_clk();
    ed_icm_mode get_mode();
};
