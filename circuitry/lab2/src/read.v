`timescale 1ns / 1ps
`define START 4
`define END 12
module read (
    clk,
	sdo,
	data,
	cs,
	sck,
	read_flag
    );
	
	input clk;
	input sdo; //for PmodALS
	
	output data;
	output cs; //for PmodALS
	output sck; //for PmodALS
	output read_flag;
	
	reg cs = 0;
	reg read_flag = 0;
	reg[7:0] data = 0;
	reg[3:0] counter = 15;
	reg[5:0] divider = 0;
	
	assign sck = divider[5];
	
	always @ (posedge clk)
		divider = divider + 1;
		
	always @ (sck) begin
		if(counter == 15) begin
			cs = 1;
		end else begin
			cs = 0;
		end
	end;
		
	always @ (posedge sck) begin
		if(counter != 15)
			counter = counter + 1;
		if(cs)
			counter = 0;

		if(counter == `START)
			read_flag <= 0;
		else if(counter == `END)
			read_flag <= 1;	
		if(!read_flag) begin
			data = data << 1;
			data[0] = sdo;
		end 
	
	end;
	

endmodule
