#include "Timer.h"

Timer::Timer(sc_module_name nm, int offset)
        :sc_module(nm),
         clk_i("clk_i"),
         addr_bi("addr_bi"),
         data_bi("data_bi"),
         data_bo("data_bo"),
         wr_i("wr_i"),
         rd_i("rd_i"),
         tval_o("tval_o"),

         mem_offset(offset),
         reg_tmr(0),
         reg_tval(0),
         reg_tconf(0)
{
    data_bo.initialize(0);
    tval_o.initialize(0);

    SC_METHOD(on_clk);
    sensitive << clk_i.pos();
}

void Timer::on_clk() {
    if (reg_tconf == Inc) {
        if (reg_tval < reg_tmr) {
            reg_tval++;
        } else {
            reg_tval = 0;
        }
    } else if (reg_tconf == Dec) {
        if (reg_tval > 0) {
            reg_tval--;
        } else {
            reg_tval = reg_tmr;
        }
    }

    if (wr_i.read()) {
        auto addr = addr_bi.read() - mem_offset;
        switch (addr) {
            case Tconf: {
                reg_tconf = data_bi.read();
                break;
            }
            case Tval: {
                reg_tval = data_bi.read();
                break;
            }
            case Tmr: {
                reg_tmr = data_bi.read();
                break;
            }
        }
    }
    tval_o.write(reg_tval);

    if (rd_i.read()) {
        auto addr = addr_bi.read() - mem_offset;
        switch (addr) {
            case Tconf: {
                data_bo.write(reg_tconf);
                break;
            }
            case Tmr: {
                data_bo.write(reg_tmr);
                break;
            }
            case Tval: {
                data_bo.write(reg_tval);
                break;
            }
        }
    }
}
