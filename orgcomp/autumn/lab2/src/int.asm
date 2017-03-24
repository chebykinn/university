DIV_VARS SEGMENT DATA
RSEG DIV_VARS
DIVIDEND: DS 2H 
DIVISOR: DS 1H 
QUOTIENT: DS 2H 
REMAINDER: DS 1H
	
dseg at 0x20
	x: ds 2
	
cseg at 0x00
	mov SP, #0x28
	jmp x_to_P2P3

P0P1_to_x:
	
	;(P0 & 0x0f) * 100
	mov a, P0
	jnb ACC.4, skip
	setb 2Fh.0 ;if (P0 & 0x10) c = 1;
	skip:
	anl a, #0x0F
	mov b, #0x64
	mul ab
	mov r0, b
	mov r1, a
	;(P1 >> 4) * 10
	mov a, P1
	anl a, #0xF0
	swap a
	mov b, #0x0a
	mul ab
	mov r2, a
	;(P1 & 0x0f)
	mov a, P1
	anl a, #0x0f
	add a, r2
	add a, r1
	mov r1, a
	;if (P0 & 0x10) x *= -1;
	jnb 2Fh.0, skip2
	mov a, r1
	cpl a
	add a, #0x01
	mov r1, a
	mov a, r0
	cpl a
	addc a, #0x00
	mov r0, a
	skip2:
	mov x, r0
	mov x + 1, r1
	jmp finish

x_to_P2P3:
	mov x, #0xFC
	mov x + 1, #0x39
	
	;if (x & 0x8000) { x *= -1; P2 = 0xd0; }
	mov P2, #0xC0
	mov a, x
	jnb ACC.7, skip3
	mov a, x + 1
	cpl a
	add a, #0x01
	mov x + 1, a
	mov a, x
	cpl a
	addc a, #0x00
	mov x, a
	mov P2, #0xD0
	skip3:
	;P2 |= x / 100;
	mov DIVIDEND, x + 1
	mov DIVIDEND + 1, x
	mov DIVISOR, #0x64
	call D16BY8
	mov a, P2
	orl a, QUOTIENT
	mov P2, a
	;P3 = (((x % 100) / 10) << 4) | x % 10;
	mov DIVIDEND, REMAINDER
	mov DIVIDEND + 1, #0x00
	mov DIVISOR, #0x0A
	call D16BY8
	mov a, QUOTIENT
	anl a, #0x0F
	swap a
	orl a, REMAINDER
	mov P3, a
	jmp finish
	

D16BY8:	CLR	A
	CJNE	A,DIVISOR,OK

DIVIDE_BY_ZERO:
	SETB	OV
	RET

OK:	MOV	QUOTIENT,A
	MOV	R4,#8
	MOV	R5,DIVIDEND
	MOV	R6,DIVIDEND+1
	MOV	R7,A

	MOV	A,R6
	MOV	B,DIVISOR
	DIV	AB
	MOV	QUOTIENT+1,A
	MOV	R6,B

TIMES_TWO:
	MOV	A,R5
	RLC	A
	MOV	R5,A
	MOV	A,R6
	RLC	A
	MOV	R6,A
	MOV	A,R7
	RLC	A
	MOV	R7,A

COMPARE:
	CJNE	A,#0,DONE
	MOV	A,R6
	CJNE	A,DIVISOR,DONE
	CJNE	R5,#0,DONE
DONE:	CPL	C

BUILD_QUOTIENT:
	MOV	A,QUOTIENT
	RLC	A
	MOV	QUOTIENT,A
	JNB	ACC.0,LOOP

SUBTRACT:
	MOV	A,R6
	SUBB	A,DIVISOR
	MOV	R6,A
	MOV	A,R7
	SUBB	A,#0
	MOV	R7,A

LOOP:	DJNZ	R4,TIMES_TWO

	MOV	A,DIVISOR
	MOV	B,QUOTIENT
	MUL	AB
	MOV	B,A
	MOV	A,DIVIDEND
	SUBB	A,B
	MOV	REMAINDER,A
	CLR	OV
	RET
	
	finish:
END
