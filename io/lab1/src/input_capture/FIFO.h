#pragma once

#include <systemc.h>

#define BUF_SIZE 32
#define MAX_BUF_IDX (BUF_SIZE - 1)

SC_MODULE(FIFO) {
    enum ic_addresses {
        ICCONF = 0x18,
        ICBUF = 0x1C
    };

    sc_in<bool>         clk_i;
    sc_in<sc_uint<32>>  addr_bi;
    sc_in<sc_uint<32>>  data_bi;
    sc_out<sc_uint<32>> data_bo;
    sc_in<bool>         wr_i;
    sc_in<bool>         rd_i;

    sc_in<bool>         ins_i;
    sc_in<sc_uint<32>>  icconf_i;
    sc_out<sc_uint<32>> icconf_o;
    sc_in<sc_uint<2>>   ictmr_i;
    sc_out<bool>        icbne_o;
    sc_out<bool>        icov_o;

    sc_in<sc_uint<32>>  tval1_i;
    sc_in<sc_uint<32>>  tval2_i;

    SC_HAS_PROCESS(FIFO);

    FIFO(sc_module_name nm);
    ~FIFO() = default;

    private:
    sc_uint<32> icbuf[BUF_SIZE] = {0};
    int reg_next_idx;
    int reg_fst_idx;

    bool reg_icov;
    bool reg_icbne;

    void on_clk();
};
