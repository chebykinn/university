`timescale 1ns / 1ps

module write_led(
	input clk,
	input sw,
	input [7:0] data,
	output [15:0] led = 4'hFFFF
	);
	always @(negedge clk) begin
		if( sw == 0 ) begin
			if( data > 0 ) begin 
				led = 4'h0000;
			end;
		end else begin
			led = data;
		end;
	end;

endmodule
