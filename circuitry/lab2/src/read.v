`timescale 1ns / 1ps
`define START 3
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
	
	reg sck;
	reg cs = 0;
	reg read_flag = 0;
	reg[7:0] data = 0;
	reg[3:0] counter = 15;
	
	always @ (clk) sck = clk;
		
	always @ (posedge clk) begin
		if(counter == 15) begin
			cs = 1;
		end else begin
			cs = 0;
			if(counter > `START && counter < `END) begin
				data = data << 1;
				data[0] = sdo;
			end else if(counter == `START)
				read_flag <= 0;
			else if(counter == `END)
				read_flag <= 1;	
		end
		counter = counter + 1;
	end;
	

endmodule
