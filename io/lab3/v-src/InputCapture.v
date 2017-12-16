module InputCapture(clk_i,rst_i,ins_i,addr_bi,data_bi,data_bo,en_i,we_bi,timer1_i,timer2_i);
input clk_i;
input rst_i;
input ins_i;
input [12:0] addr_bi;
input [31:0] data_bi;
output [31:0] data_bo;
input en_i;
input we_bi;
input [31:0] timer1_i;
input [31:0] timer2_i;


wire icov_fifo_icconf;
wire icbne_fifo_icconf;
wire [31:0] tval_tc_fifo;
wire ins_prescaler_fifo;
wire ins_ed_prescaler;
wire [31:0] icconf_fifo_icconf;
wire [1:0] ictmr_from_icconf;
wire [2:0] icm_from_icconf;
wire [31:0] icconf_icconf_fifo;

FIFO fifo (.tval2_i(timer2_i), .tval1_i(tval_tc_fifo), .icov_o(icov_fifo_icconf), .icbne_o(icbne_fifo_icconf), .ictmr_i(ictmr_from_icconf), .icconf_o(icconf_fifo_icconf), .icconf_i(icconf_icconf_fifo), .ins_i(ins_prescaler_fifo), .we_bi(we_bi), .en_i(en_i), .data_bo(data_bo), .data_bi(data_bi), .addr_bi(addr_bi), .rst_i(rst_i), .clk_i(clk_i));
TimersControl timers_control (.tval_o(tval_tc_fifo), .ictmr_i(ictmr_from_icconf), .timer2_i(timer2_i), .timer1_i(timer1_i));
Prescaler prescaler (.ins_o(ins_prescaler_fifo), .ins_i(ins_ed_prescaler), .icm_i(icm_from_icconf), .rst_i(rst_i), .clk_i(clk_i));
EdgeDetector edge_detector (.ins_o(ins_ed_prescaler), .ins_i(ins_i), .icm_i(icm_from_icconf), .clk_i(clk_i));
ICCONF icconf (.ictmr_o(ictmr_from_icconf), .icm_o(icm_from_icconf), .icov_i(icov_fifo_icconf), .icbne_i(icbne_fifo_icconf), .icconf_o(icconf_icconf_fifo), .icconf_i(icconf_fifo_icconf));


endmodule
