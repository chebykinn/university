`timescale 1ns / 1ps
`define TPS (100000000)
`define NUM_OF_PIECES 100
`define PIECE (`TPS / `NUM_OF_PIECES)
module controller(
	clk,
	reset,
	red,
	blue,
	green
    );
    
    input clk;
    input reset;
   
    output red;
    output blue;
    output green;
	 
	 
    wire clk;
    wire reset;
    wire red;
    wire blue;
    wire green;
	 
    reg [31:0] seconds_counter = 0;
	 reg [31:0] piece_counter = 0;
	 reg signed [31:0] border = 0;

	 pwm red_pwm (
		.clk(clk), 
		.reset(reset), 
		.border(border), 
		.out(red)
	 );
	 
	 pwm blue_pwm (
		.clk(clk), 
		.reset(reset), 
		.border(border - `PIECE), 
		.out(blue)
	 );

	 pwm green_pwm (
		.clk(clk), 
		.reset(reset), 
		.border(border - 2 * `PIECE), 
		.out(green)
	 );	 
    always @ (posedge clk) begin
		if(reset || (seconds_counter >= `TPS * 6)) begin
				border = 0;
				seconds_counter = 0;
				piece_counter = 0;
		end else begin
			seconds_counter = seconds_counter + 1;
			piece_counter = piece_counter + 1;
			if(piece_counter >= `NUM_OF_PIECES) begin
				piece_counter = 0;
				if(seconds_counter >= `TPS * 3)
					border = border - 1;
				else
					border = border + 1;
			end;
    	end;

	end;
	
endmodule
