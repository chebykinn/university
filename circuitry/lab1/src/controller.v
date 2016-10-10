`timescale 1ns / 1ps
`define TPS (100000)
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
    reg red;
    reg blue;
    reg green;
	 
    reg [31:0] seconds_counter = 0;
    reg [31:0] steps_counter = 0;
	 reg signed [31:0] border = 0;

	
    always @ (posedge clk)
    begin
		if(reset || (seconds_counter >= `TPS * 6)) begin
				border = 0;
				seconds_counter = 0;
				steps_counter = 0;
		end else begin
			seconds_counter = seconds_counter + 1;
			if(seconds_counter % `NUM_OF_PIECES == 0) begin
				if(seconds_counter >= `TPS * 3)
					border = border - 1;
				else
					border = border + 1;
			end;
			steps_counter = steps_counter + 1;
			if(steps_counter >= `PIECE)
				steps_counter = 0;
    	end;

		if(steps_counter < border)
			red <= 1;
		else
			red <= 0;
		if(steps_counter + `PIECE < border)
			blue <= 1;
		else
			blue <= 0;
		if(steps_counter + 2 * `PIECE < border)
			green <= 1;
		else
			green <= 0;
	end;
	
endmodule
