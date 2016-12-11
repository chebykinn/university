`timescale 1ns / 1ps

module write_led (
	clk,
	data,
	comp_data,
	switch,
	led
	);
	
	input clk;
	input data;
	input comp_data;
	input switch;
	
	output led;
	
	wire[7:0] data;
	wire[7:0] comp_data;
	
	reg[15:0] led = 16'hFFFF;
	
	always @(posedge clk) begin
		if( switch == 0 ) begin
			if( data > comp_data )
				led = 16'h0000;
			else
				led = 16'hFFFF;		
		end else begin
			led = data;
		end;
	end;

endmodule
