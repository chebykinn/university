#pragma once

#include "systemc.h"

SC_MODULE(Timer) {
    enum timer_addresses {
        Tmr     = 0x00000000,
        Tval    = 0x00000004,
        Tconf   = 0x00000008
    };

    enum timer_conf {
        Inc = 0x2,
        Dec = 0x3
    };
    
    sc_in<bool>         clk_i;
    sc_in<sc_uint<32>>  addr_bi;
    sc_in<sc_uint<32>>  data_bi;
    sc_out<sc_uint<32>> data_bo;
    sc_in<bool>         wr_i;
    sc_in<bool>         rd_i;
    sc_out<sc_uint<32>> tval_o;

    SC_HAS_PROCESS(Timer);
    
    Timer(sc_module_name nm, int start_addr);
    ~Timer() = default;
    
private:
    void on_clk();

    int mem_offset;

    sc_uint<32> reg_tmr;
    sc_uint<32> reg_tval;
    sc_uint<32> reg_tconf;
};
