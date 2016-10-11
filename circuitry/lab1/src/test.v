`timescale 1ns / 1ps
module test;

	// Inputs
	reg clk;
	reg reset;

	// Outputs
	wire red;
	wire blue;
	wire green;

	// Instantiate the Unit Under Test (UUT)
	controller uut (
		.clk(clk), 
		.reset(reset), 
		.red(red), 
		.blue(blue), 
		.green(green)
	);

	event reset_trigger;
	
	initial begin 
		forever begin
			@ (reset_trigger);
			@ (negedge clk);
			reset = 1; 
			@ (negedge clk); 
			reset = 0; 
		end 
	end

	initial begin
		// Initialize Inputs
		clk = 0;
		reset = 0;
		
		#500000 -> reset_trigger;
		
	end
	
	always begin
		#1 clk = ~clk;
	end
      
endmodule

