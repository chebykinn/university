#pragma once

#include <systemc.h>

SC_MODULE(MIPS32) {
    sc_in<bool>         clk_i;
    sc_out<sc_uint<32>> addr_bo;
    sc_in<sc_uint<32>>  data_bi;
    sc_out<sc_uint<32>> data_bo;
    sc_out<bool>        wr_o;
    sc_out<bool>        rd_o;
    
    SC_HAS_PROCESS(MIPS32);
    
    MIPS32(sc_module_name nm);
    ~MIPS32() = default;
    
    void mainThread();

private:
    void bus_write(sc_uint<32> addr, sc_uint<32> data);
    sc_uint<32> bus_read(sc_uint<32> addr);

    sc_uint<4>  mips_state;
    sc_uint<2>  mips_inner_state;
};
