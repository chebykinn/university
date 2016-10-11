`timescale 1ns / 1ps
`define TPS (100000)
`define NUM_OF_PIECES 100
`define PIECE (`TPS / `NUM_OF_PIECES)
//////////////////////////////////////////////////////////////////////////////////
// Company: 
// Engineer: 
// 
// Create Date:    07:34:42 10/11/2016 
// Design Name: 
// Module Name:    pwm 
// Project Name: 
// Target Devices: 
// Tool versions: 
// Description: 
//
// Dependencies: 
//
// Revision: 
// Revision 0.01 - File Created
// Additional Comments: 
//
//////////////////////////////////////////////////////////////////////////////////
module pwm(
   clk,
	reset,
   border,
   out
   ); 
	 
	input clk;
	input reset;
   input border;
   output out;
	 
	wire clk;
	wire reset;
	wire [31:0] border;
	reg out;
	 
   reg [15:0] steps_counter = 0;

	
   always @ (posedge clk) begin
		if(reset || steps_counter >= `PIECE)
			steps_counter = 0;
		else 
			steps_counter = steps_counter + 1;
		
		if(steps_counter < border)
			out = 1;
		else
			out = 0;
		
	end
	 

endmodule
