`timescale 1ns / 1ps

module write_led(
	sdo,
	clk,
	sw,
	cs,
	sck,
	led
	);
	
	input sdo; //for PmodALS
	input clk;
	input sw;
	
	output cs; //for PmodALS
	output sck; //for PmodALS
	output led;
	
	wire[7:0] data;
	
	reg[15:0] led = 16'hFFFF;
	
	reader read (
		.clk(clk),
		.sdo(sdo),
		.data(data),
		.cs(cs),
		.sck(sck)
	);
	
	always @(negedge clk) begin
		if( sw == 0 ) begin
			if( data > 0 )
				led <= 16'h0000;
			else
				led <= 16'hFFFF;
				
		end else begin
			led <= data;
		end;
	end;

endmodule
