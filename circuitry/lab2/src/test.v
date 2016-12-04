`timescale 1ns / 1ps


module test;

	// Inputs
	reg clk;
	reg sdo;
	reg switch = 0;
	reg reset = 0;
	reg [7:0] comp_data = 8'h0F;
	reg [15:0] test_input = 16'b0001111011100000;
	reg [3:0] i = 15;
	
	wire [15:0] led;
	wire sck;

	// Instantiate the Unit Under Test (UUT)
	lab2 uut (
		.clk(clk), 
		.sdo(sdo),
		.reset(reset),
		.switch(switch),
		.comp_data(comp_data),
		.sck(sck),
		.led(led),
		.cs(cs)
	);
	
	event reset_trigger;
	
	initial begin 
		forever begin
			@ (reset_trigger);
			reset = 1; 
			#20 reset = 0; 
		end 
	end
	
	initial begin
		// Initialize Inputs
		clk = 0;
		switch = 0;
		sdo = 0;
		
		#130 -> reset_trigger;
	end
      
	always begin
		#0.01 clk = ~clk;
	end
	
	always begin
		#100 switch <= ~switch;
	end
	
	always @(negedge sck) begin
		if(!cs && i > 0) begin
			sdo = test_input[i];
			i = i - 1;
		end else
			i = 15;
	end
endmodule

