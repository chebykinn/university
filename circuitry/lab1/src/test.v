`timescale 1ns / 1ps

////////////////////////////////////////////////////////////////////////////////
// Company: 
// Engineer:
//
// Create Date:   04:21:51 09/27/2016
// Design Name:   controller
// Module Name:   C:/Users/Berserk/Dropbox/circuitry/2 sem/lab1/test.v
// Project Name:  lab1
// Target Device:  
// Tool versions:  
// Description: 
//
// Verilog Test Fixture created by ISE for module: controller
//
// Dependencies:
// 
// Revision:
// Revision 0.01 - File Created
// Additional Comments:
// 
////////////////////////////////////////////////////////////////////////////////

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

	initial begin
		// Initialize Inputs
		clk = 0;
		reset = 0;

	end
	
	always begin
		#1 clk = ~clk;
	end
      
endmodule

