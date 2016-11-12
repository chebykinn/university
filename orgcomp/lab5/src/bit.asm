value equ b
mem equ r0
z bit acc.0

x1 bit value.0
x2 bit value.1
x3 bit value.2
x4 bit value.3

cseg at 0
	jmp start

start:
	mov value, #0

loop:
	mov A, mem
	add A, ACC

	mov C, x2
	orl C, /x3
	anl C, /x1
	mov z ,C
	mov C, x4
	anl C, x1
	orl C, z
	mov z, C

	mov mem, A
	INC value
	mov A, value
	cjne A, #0x08, not8	

	mov P0, mem
	sjmp loop

not8:
	cjne A, #0x10, loop
	
	mov P1, mem
	end
