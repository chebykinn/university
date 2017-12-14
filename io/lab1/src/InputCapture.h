#pragma once

#include "systemc.h"
#include "input_capture/ICCONF.h"
#include "input_capture/Prescaler.h"
#include "input_capture/EdgeDetector.h"
#include "input_capture/TimersControl.h"
#include "input_capture/FIFO.h"

SC_MODULE(InputCapture) {
    sc_in<bool>         clk_i;
    sc_in<bool>         ins_i;
    sc_in<sc_uint<32>>  addr_bi;
    sc_in<sc_uint<32>>  data_bi;
    sc_out<sc_uint<32>> data_bo;
    sc_in<bool>         wr_i;
    sc_in<bool>         rd_i;

    sc_in<sc_uint<32>>  timer1_i;
    sc_in<sc_uint<32>>  timer2_i;

    SC_HAS_PROCESS(InputCapture);
    
    InputCapture(sc_module_name nm);
    ~InputCapture() = default;


    sc_signal<sc_uint<32>>  icconf_icconf_fifo;
    sc_signal<sc_uint<3>>   icm_from_icconf;
    sc_signal<sc_uint<2>>   ictmr_from_icconf;

    sc_signal<sc_uint<32>>  icconf_fifo_icconf;

    sc_signal<bool>         ins_ed_prescaler;

    sc_signal<bool>         ins_prescaler_fifo;

    sc_signal<sc_uint<32>>  tval_tc_fifo;

    sc_signal<bool>         icbne_fifo_icconf;
    sc_signal<bool>         icov_fifo_icconf;
private:
    ICCONF          icconf;
    Prescaler       prescaler;
    EdgeDetector    edge_detector;
    TimersControl   timers_control;
    FIFO            fifo;
};
