`timescale 1ns / 1ps

module write_led (
	clk,
	data,
	cmprtr_data,
	switch,
	led
	);
	
	input clk;
	input data;
	input cmprtr_data;
	input switch;
	
	output led;
	
	wire[7:0] data;
	wire[7:0] cmprtr_data;
	
	reg[15:0] led = 16'hFFFF;
	
	always @(posedge clk or switch) begin
		if( switch == 0 ) begin
			if( data > cmprtr_data )
				led = 16'h0000;
			else
				led = 16'hFFFF;		
		end else begin
			led = data;
		end;
	end;

endmodule
