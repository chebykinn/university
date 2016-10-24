`timescale 1ns / 1ps

module test;

	// Inputs
	reg clk;
	reg sdo;
	reg sw;
	wire [7:0] data;
	
	reg cs;
	reg sck;
	reg [15:0] test_input = 16'b0001111111100000;
	reg [4:0] i = 15;
	
	wire [15:0] led;

	reader read (
		.clk(clk),
		.sdo(sdo),
		.data(data),
		.cs(cs),
		.sck(sck)
	);
	// Instantiate the Unit Under Test (UUT)
	write_led uut (
		.clk(clk), 
		.sw(sw), 
		.data(data), 
		.led(led)
	);

	initial begin
		// Initialize Inputs
		clk = 0;
		sw = 0;
		//sw = 1;
		sdo = 0;
		cs = 0;
	end
      
	always begin
		#1 clk = ~clk;
	end
	
	always @(posedge clk) begin
		sdo = test_input[i];
		i = i - 1;
	end
endmodule

