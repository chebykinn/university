DIV_VARS SEGMENT DATA
RSEG DIV_VARS

BIN: DS 2H

cseg at 0x00
	mov a, P1
	anl a, #0x0f
	mov r0, a
	mov a, P1
	swap a
	anl a, #0x0f
	mov b, #0x0a
	mul ab
	add a, r0
	mov r0, a

	mov r3, #0x01
	mov a, P0
	anl a, #0xf0
	subb a, #0xd0
	jnz n_sign
	mov r3, #0xff

n_sign:
	mov a, P0
	mov b, #0x0a
	anl a, #0x0f
	mul ab
	mov b, #0x64
	mul ab
	add a, r0
	mov r0, a
	mov a, b
	addc a, #0x00
	mov b, r3
	mul ab
	mov P2, a
	mov a, r0
	mov b, r3
	mul ab
	mov P3, a


	jnb
	
END
