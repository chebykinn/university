#include "mips32.h"
#include "Timer.h"
#include "BusMatrix.h"
#include "InputCapture.h"
#include "Signal.h"

int sc_main(int argc, char* argv[]) {
    
    MIPS32 mips32_core("mips32");
    BusMatrix busMatrix("busMatrix");

    Timer timer1("timer1", FIRST_TIMER_OFFSET);
    Timer timer2("timer2", SECOND_TIMER_OFFSET);
    InputCapture input_capture("input_capture");

    Signal signal_block("signal_block");

    sc_clock clk("clk", sc_time(10, SC_NS));
    sc_signal<sc_uint<32>>  addr;
    sc_signal<sc_uint<32>>  data_mips32_bo;
    sc_signal<sc_uint<32>>  data_mips32_bi;
    sc_signal<bool> wr;
    sc_signal<bool> rd;

    sc_signal<sc_uint<32>>  data_timer1_bm;
    sc_signal<bool>         rd_bm_timer1;
    sc_signal<bool>         wr_bm_timer1;
    sc_signal<sc_uint<32>>  data_timer2_bm;
    sc_signal<bool>         rd_bm_timer2;
    sc_signal<bool>         wr_bm_timer2;
    sc_signal<sc_uint<32>>  data_ic_bm;
    sc_signal<bool>         rd_bm_ic;
    sc_signal<bool>         wr_bm_ic;

    sc_signal<sc_uint<32>>  tval_timer1_ic;
    sc_signal<sc_uint<32>>  tval_timer2_ic;

    sc_signal<bool> signal;

    mips32_core.clk_i(clk);
    mips32_core.addr_bo(addr);
    mips32_core.data_bi(data_mips32_bi);
    mips32_core.data_bo(data_mips32_bo);
    mips32_core.wr_o(wr);
    mips32_core.rd_o(rd);

    busMatrix.clk_i(clk);
    busMatrix.addr_bi(addr);
    busMatrix.rd_i(rd);
    busMatrix.wr_i(wr),
    busMatrix.data_bo(data_mips32_bi);
    busMatrix.timer1_data_bi(data_timer1_bm);
    busMatrix.timer1_rd_o(rd_bm_timer1);
    busMatrix.timer1_wr_o(wr_bm_timer1);
    busMatrix.timer2_data_bi(data_timer2_bm);
    busMatrix.timer2_rd_o(rd_bm_timer2);
    busMatrix.timer2_wr_o(wr_bm_timer2);
    busMatrix.input_capture_data_bi(data_ic_bm);
    busMatrix.input_capture_rd_o(rd_bm_ic);
    busMatrix.input_capture_wr_o(wr_bm_ic);

    timer1.clk_i(clk);
    timer1.addr_bi(addr);
    timer1.data_bi(data_mips32_bo);
    timer1.data_bo(data_timer1_bm);
    timer1.wr_i(wr_bm_timer1);
    timer1.rd_i(rd_bm_timer1);
    timer1.tval_o(tval_timer1_ic);

    timer2.clk_i(clk);
    timer2.addr_bi(addr);
    timer2.data_bi(data_mips32_bo);
    timer2.data_bo(data_timer2_bm);
    timer2.wr_i(wr_bm_timer2);
    timer2.rd_i(rd_bm_timer2);
    timer2.tval_o(tval_timer2_ic);

    signal_block.data_o(signal);

    input_capture.clk_i(clk);
    input_capture.ins_i(signal);
    input_capture.addr_bi(addr);
    input_capture.data_bi(data_mips32_bo);
    input_capture.data_bo(data_ic_bm);
    input_capture.wr_i(wr_bm_ic);
    input_capture.rd_i(rd_bm_ic);
    input_capture.timer1_i(tval_timer1_ic);
    input_capture.timer2_i(tval_timer2_ic);

    sc_trace_file *wf = sc_create_vcd_trace_file("wave");
    wf->set_time_unit(100, SC_PS);
    sc_trace(wf, clk, "clk");
    sc_trace(wf, addr, "addr_bo");
    sc_trace(wf, data_mips32_bi, "data_bi");
    sc_trace(wf, data_mips32_bo, "data_bo");
    sc_trace(wf, wr, "wr");
    sc_trace(wf, rd, "rd");

//    sc_trace(wf, input_capture.icconf_icconf_fifo, "ic.icconf_icconf_fifo");
//    sc_trace(wf, input_capture.icm_from_icconf, "ic.icm_from_icconf");
//    sc_trace(wf, input_capture.ictmr_from_icconf, "ic.ictmr_from_icconf");
//    sc_trace(wf, input_capture.icconf_fifo_icconf, "ic.icconf_fifo_icconf");
//    sc_trace(wf, input_capture.icov_fifo_icconf, "ic.icov_fifo_icconf");
//    sc_trace(wf, input_capture.icbne_fifo_icconf, "ic.icbne_fifo_icconf");
    sc_trace(wf, input_capture.ins_ed_prescaler, "ic.ins_ed_prescaler");
    sc_trace(wf, input_capture.ins_prescaler_fifo, "ic.ins_prescaler_fifo");
//    sc_trace(wf, input_capture.tval_tc_fifo, "ic.tval_tc_fifo");
//    sc_trace(wf, input_capture.icbne_fifo_icconf, "ic.icbne_fifo_icconf");
//    sc_trace(wf, input_capture.icov_fifo_icconf, "ic.icov_fifo_icconf");

    sc_trace(wf, data_timer1_bm, "data_timer1_bm");
    sc_trace(wf, rd_bm_timer1, "rd_bm_timer1");
    sc_trace(wf, wr_bm_timer1, "wr_bm_timer1");
    sc_trace(wf, data_timer2_bm, "data_timer2_bm");
    sc_trace(wf, rd_bm_timer2, "rd_bm_timer2");
    sc_trace(wf, wr_bm_timer2, "wr_bm_timer2");
    sc_trace(wf, data_ic_bm, "data_ic_bm");
    sc_trace(wf, rd_bm_ic, "rd_bm_ic");
    sc_trace(wf, wr_bm_ic, "wr_bm_ic");

    sc_trace(wf, tval_timer1_ic, "tval1_out");
    sc_trace(wf, tval_timer2_ic, "tval2_out");

    sc_trace(wf, signal, "signal");

    sc_start();

 
    sc_close_vcd_trace_file(wf);
    
    return(0);
    
}
