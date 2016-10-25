`timescale 1ns / 1ps


module test;

	// Inputs
	reg clk;
	reg sdo;
	reg sw;
	reg pf;
	reg [15:0] test_input = 16'b0001111011100000;
	reg [3:0] i;
	
	wire [15:0] led;
	wire sck;

	// Instantiate the Unit Under Test (UUT)
	write_led uut (
		.sdo(sdo),
		.clk(clk), 
		.sw(sw), 
		.cs(cs),
		.sck(sck),
		.led(led)
	);
	
	event sw_trigger;
	
	initial begin 
		forever begin
			@ (sw_trigger);
			@ (posedge clk); 
			sw = ~sw; 
		end 
	end
	
	initial begin
		// Initialize Inputs
		pf = 0;
		clk = 0;
		sw = 0;
		sdo = 0;
	end
      
	always begin
		#1 clk = ~clk;
	end
	
	always begin
		#100 sw <= ~sw;
	end
	
	always @(posedge cs)
		pf = 0;
		
	always @(negedge cs)
		pf = 1;
	
	always @(posedge sck) begin
		if(pf && i > 0) begin
			sdo = test_input[i];
			i = i - 1;
		end else
			i = 15;
	end
endmodule

