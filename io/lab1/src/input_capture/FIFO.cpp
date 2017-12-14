#include "FIFO.h"
#include "ICCONF.h"

FIFO::FIFO(sc_module_name nm)
        :sc_module(nm),
         clk_i("clk_i"),
         addr_bi("addr_bi"),
         data_bi("data_bi"),
         data_bo("data_bo"),
         wr_i("wr_i"),
         rd_i("rd_i"),

         ins_i("ins_i"),
         icconf_i("icconf_i"),
         icconf_o("icconf_o"),
         ictmr_i("ictmr_i"),
         icbne_o("icbne_o"),
         icov_o("icov_o"),
         tval1_i("tval1_i"),
         tval2_i("tval2_i") {
    icconf_o.initialize(0);
    icbne_o.initialize(0);
    icov_o.initialize(ICCONF::FIFONotFull);

    reg_icov = ICCONF::FIFONotFull;
    reg_icbne = ICCONF::FIFOEmpty;

    reg_fst_idx = 0;
    reg_next_idx = reg_fst_idx;

    SC_METHOD(on_clk);
    sensitive << clk_i.pos();
}

int get_next_idx(int idx) {
    if (idx == MAX_BUF_IDX) {
        return 0;
    } else {
        return ++idx;
    }
}

void FIFO::on_clk() {
    if (ins_i.read() && reg_icov != ICCONF::FIFOFull) {
        sc_uint<32> temp_value = 0;

        switch (ictmr_i.read()) {
            case ICCONF::ICTimersOff: {
                return;
            }
            case ICCONF::ICTimersFirst:
            case ICCONF::ICTimersSecond: {
                temp_value.range(15, 0) = tval1_i.read().range(15, 0);
                break;
            }
            case ICCONF::ICTimersBoth: {
                temp_value.range(15, 0) = tval1_i.read().range(15, 0);
                temp_value.range(31, 16) = tval2_i.read().range(15, 0);
                break;
            }
        }
        icbuf[reg_next_idx] = temp_value;
        reg_next_idx = get_next_idx(reg_next_idx);

        reg_icbne = ICCONF::FIFONotEmpty;
        reg_icov = (reg_fst_idx == reg_next_idx) ? ICCONF::FIFOFull : ICCONF::FIFONotFull;
    }
    if (wr_i.read()) {
        switch (addr_bi.read()) {
            case ICCONF: {
                icconf_o.write(data_bi.read());
                break;
            }
        }
    }
    if (rd_i.read()) {
        switch (addr_bi.read()) {
            case ICCONF: {
                data_bo.write(icconf_i.read());
                break;
            }
            case ICBUF: {
                if (reg_icbne != ICCONF::FIFOEmpty) {
                    data_bo.write(icbuf[reg_fst_idx]);
                    reg_fst_idx = get_next_idx(reg_fst_idx);
                    reg_icov = ICCONF::FIFONotFull;
                }
                break;
            }
        }
    }
    reg_icbne = (reg_next_idx == reg_fst_idx) ? ICCONF::FIFOEmpty : ICCONF::FIFONotEmpty;

    icbne_o.write(reg_icbne);
    icov_o.write(reg_icov);
}

