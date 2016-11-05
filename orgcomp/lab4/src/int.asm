Dseg at 0x08
	ai: ds 1
	s: ds 1
	x equ r0

	cseg at 0x0
	jmp start

Si1: 
	mov b, x
	mul ab
	mov a, ai
	mul ab
	mov a, #0xff
	subb a, b
	ret

start: 
	mov x,#0

cikl: 
	mov a,#0x01
	mov ai, #142
	call Si1
	mov ai, #128
	call Si1
	mov ai, #64
	call Si1
	mov b, x
	mul ab
	mov a, #128
	mul ab
	mov P3, b
		inc x
		jmp cikl
		nop
	end
