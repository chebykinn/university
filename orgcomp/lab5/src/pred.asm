Dseg at 0x10
	aa equ r7
	bb equ r6
	cc equ r5
	dd equ r4
	ii equ r3

	cseg at 0x0
jmp start
	
compare:
	clr c
	xrl a, #0x80 ; s.bb^1.00 инверсия знака
	mov r0, a
	mov a, b
	xrl a, #0x80 ; s.aa^1.00 инверсия знака
	subb a, r0 ; s.dd - s.aa
	ret

start:
	mov ii, 0
	mov a, ii

loop:
	mov a, P1
	add a, ACC
	mov P1, a

	anl a, #0x08
	mov aa, a

	mov a, ii
	anl a, #0x04
	mov bb, a

	mov a, ii
	anl a, #0x02
	mov cc, a

	clr c
	mov a, ii
	subb a, #0x04
	mov dd, a

;(aa > dd)
	mov a, aa
	mov b, dd
	call compare
	jnc M1 ; if(aa>dd) goto M1
	
;(aa > dd) & (bb != cc) 
	mov a, cc
	clr c
	subb a, bb
	jnz M2 ; if(cc!=dd) goto M3

;(dd >= cc)
M1: 
	mov a, dd
	mov b, cc
	call compare
	jz M2
	jnc M3 ; if (bb==0) goto M3
M2: 
	mov r1, #0x01
	sjmp M4
M3: 
	mov r1, #0x00 ; S=0
M4: 
	mov a, P1
	orl a, r1 ; S=1
	mov P1, a
	
	inc ii
	mov a, ii
	cjne a, #0x08, continue
	mov P0, P1
	sjmp loop
continue:	
	cjne a, #0x10, loop
end
