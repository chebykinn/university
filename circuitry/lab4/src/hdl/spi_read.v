`timescale 1ns / 1ps
`define START 4
`define END 12

module spi_read (
   clk,
	sdo,
	reset,
	data,
	cs,
	sck,
	read_flag
    );
	
	input clk;
	input sdo; //for PmodALS
	input reset;
	
	output data;
	output cs; //for PmodALS
	output sck; //for PmodALS
	output read_flag;
	
	reg read_flag = 0;
	reg[7:0] data = 0;
	reg[3:0] counter = 15;
	reg[5:0] divider = 0;
	
	assign sck = divider[5];
	assign cs = counter == 15? 1: 0;
	
	always @ (posedge clk)
		divider = divider + 1;
		
	always @ (posedge sck) begin
		if(!cs)
			counter = counter + 1;
			
		if(reset) begin
			data = 0;
			read_flag = 1;
		end else begin
			if(counter == `START)
				read_flag = 0;
			if(counter == `END)
				read_flag = 1;
				
			if(!read_flag) begin
				data = data << 1;
				data[0] = sdo;
			end 
	
			if(cs)
				counter = 0;
		end
		
	end;
	

endmodule