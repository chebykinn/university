`timescale 1ns / 1ps


module test;

	// Inputs
	reg clk;
	reg sdo;
	reg switch = 0;
	reg pf;
	reg [7:0] comp_data = 8'h0F;
	reg [15:0] test_input = 16'b0001111011100000;
	reg [3:0] i = 15;
	reg out_cs = 0;
	
	wire cs;
	wire [15:0] led;
	wire sck;
 
	assign cs = out_cs == 1? 0: 1'bz;

	// Instantiate the Unit Under Test (UUT)
	lab2 uut (
		.clk(clk), 
		.sdo(sdo),
		.switch(switch),
		.comp_data(comp_data),
		.sck(sck),
		.led(led),
		.cs(cs)
	);
	
	event sw_trigger;
	
	initial begin 
		forever begin
			@ (sw_trigger);
			@ (posedge clk); 
			switch = ~switch; 
		end 
	end
	
	initial begin
		// Initialize Inputs
		pf = 0;
		clk = 0;
		switch = 0;
		sdo = 0;
	end
      
	always begin
		#1 clk = ~clk;
	end
	
	always begin
		#100 switch <= ~switch;
	end
	
	always @(negedge sck) begin
		if(!i) begin
			pf = 0;
			out_cs = 0;
		end
		if(cs) begin 
			out_cs = 1;
			pf = 1;
		end;
		if(pf && i > 0) begin
			sdo = test_input[i];
			i = i - 1;
		end else
			i = 15;
	end
endmodule

