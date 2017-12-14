#include "InputCapture.h"

InputCapture::InputCapture(sc_module_name nm)
        :sc_module(nm),
         clk_i("clk_i"),
         ins_i("ins_i"),
         addr_bi("addr_bi"),
         data_bi("data_bi"),
         data_bo("data_bo"),
         wr_i("wr_i"),
         rd_i("rd_i"),
         timer1_i("timer1_i"),
         timer2_i("timer2_i"),

         icconf("icconf"),
         prescaler("prescaler"),
         edge_detector("edge_detector"),
         timers_control("timers_control"),
         fifo("fifo")
{
    data_bo.initialize(0);

    icconf.icconf_i(icconf_fifo_icconf);
    icconf.icconf_o(icconf_icconf_fifo);
    icconf.icbne_i(icbne_fifo_icconf);
    icconf.icov_i(icov_fifo_icconf);
    icconf.icm_o(icm_from_icconf);
    icconf.ictmr_o(ictmr_from_icconf);

    edge_detector.clk_i(clk_i);
    edge_detector.icm_i(icm_from_icconf);
    edge_detector.ins_i(ins_i);
    edge_detector.ins_o(ins_ed_prescaler);

    prescaler.clk_i(clk_i);
    prescaler.icm_i(icm_from_icconf);
    prescaler.ins_i(ins_ed_prescaler);
    prescaler.ins_o(ins_prescaler_fifo);

    timers_control.timer1_i(timer1_i);
    timers_control.timer2_i(timer2_i);
    timers_control.ictmr_i(ictmr_from_icconf);
    timers_control.tval_o(tval_tc_fifo);

    fifo.clk_i(clk_i);
    fifo.addr_bi(addr_bi);
    fifo.data_bi(data_bi);
    fifo.data_bo(data_bo);
    fifo.wr_i(wr_i);
    fifo.rd_i(rd_i);

    fifo.ins_i(ins_prescaler_fifo);
    fifo.icconf_i(icconf_icconf_fifo);
    fifo.icconf_o(icconf_fifo_icconf);
    fifo.ictmr_i(ictmr_from_icconf);
    fifo.icbne_o(icbne_fifo_icconf);
    fifo.icov_o(icov_fifo_icconf);
    fifo.tval1_i(tval_tc_fifo);
    fifo.tval2_i(timer2_i);
}

