`timescale 1ns / 1ps

module lab2(
    clk,
    sdo,
    switch,
	comp_data,
    sck,
    led,
    cs
    );
	
	input clk;
	input sdo;
    input switch;
	input[7:0] comp_data;
	
    output sck;
    output[15:0] led;
    output cs;

	wire[7:0] data;
	wire read_flag;

read reader (
		.clk(clk),
		.sdo(sdo),
		.data(data),
		.cs(cs),
		.sck(sck),
		.read_flag(read_flag)
	);
	
write_led writer (
		.clk(read_flag),
		.data(data),
		.comp_data(comp_data),
		.switch(switch),
		.led(led)
	);


endmodule
