`timescale 1ns / 1ps
`define START 3
`define END 12
module reader(
    clk,
	sdo,
	data,
	cs,
	sck
    );
	
	input clk;
	input sdo; //for PmodALS
	
	output cs; //for PmodALS
	output sck; //for PmodALS
	output data;
	
	reg sck;
	reg cs;
	reg[7:0] tmp = 0;
	reg[7:0] data = 0;
	reg[3:0] counter = 15;
	
	always @ (clk) sck = clk;
		
	always @ (negedge clk) begin
		if(counter == 15) begin
			counter = 0;
			cs = 1;
		end else begin
			cs = 0;
			if(counter > `START && counter < `END) begin
				tmp = tmp << 1;
				tmp[0] = sdo;
			end else if(counter == `END)
				data <= tmp;	
			counter = counter + 1;
		end
	end;
	

endmodule
