`timescale 1ns / 1ps
`define START 4
`define END 13
module read (
    clk,
	sdo,
	data,
	cs,
	sck,
	read_flag
    );
	
	inout cs; //for PmodALS
	
	input clk;
	input sdo; //for PmodALS
	
	output data;
	output sck; //for PmodALS
	output read_flag;
	
	reg sck;
	reg read_flag = 0;
	reg[7:0] data = 0;
	reg[4:0] counter = 16;
	
	assign cs = counter[4];
	
	always @ (clk) sck = clk;
/*	
	always @ (clk) begin
		if(counter == 15) begin
			cs = clk;
		end else begin
			cs = 0;
		end
	end;
		
	always @ (posedge clk) begin
		if(counter != 15)
			counter = counter + 1;
		if(cs)
			counter = 0;
		if(counter > `START && counter < `END) begin
			data = data << 1;
			data[0] = sdo;
		end else if(counter == `START)
			read_flag <= 0;
		else if(counter == `END)
			read_flag <= 1;	
	end;*/
	
	always @ (posedge clk) begin
		if(!cs)
			counter = counter + 1;
		else
			counter = 0;
		if(counter > `START && counter < `END) begin
			data = data << 1;
			data[0] = sdo;
		end else if(counter == `START)
			read_flag <= 0;
		else if(counter == `END)
			read_flag <= 1;	
		end;
	

endmodule
