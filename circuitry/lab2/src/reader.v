`timescale 1ns / 1ps
//////////////////////////////////////////////////////////////////////////////////
// Company: 
// Engineer: 
// 
// Create Date:    19:38:30 10/24/2016 
// Design Name: 
// Module Name:    reader 
// Project Name: 
// Target Devices: 
// Tool versions: 
// Description: 
//
// Dependencies: 
//
// Revision: 
// Revision 0.01 - File Created
// Additional Comments: 
//
//////////////////////////////////////////////////////////////////////////////////
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
	
	always sck <= clk;
		
	always @ (negedge clk) begin
		if(counter == 15) begin
			counter = 0;
			cs <= 1;
		end	else begin
			cs <= 0;
			if((counter[2] || counter[3]) && counter < 13) begin
				tmp = tmp << 1;
				tmp[0] = sdo;
			end else if(counter == 13)
				data = tmp;	
			counter = counter + 1;
		end
	end;
	

endmodule
