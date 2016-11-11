Dseg at 0x10
	aa: ds 1
	bb: ds 1
	cc: ds 1
	dd: ds 1
	ii: ds 1

	cseg at 0x0
jmp start
	
compare:
	clr c
	xrl a, #0x80 ; s.bb^1.00 инверсия знака
	mov r0, a
	mov a, b
	xrl a, #0x80 ; s.aa^1.00 инверсия знака
	subb a, r0 ; s.dd – s.aa
	ret

start:
	mov ii, 0

loop:
	mov a, ii
	anl a, #0x08
	mov aa, a

	mov a, ii
	anl a, #0x04
	mov bb, a

	mov a, ii
	anl a, #0x02
	mov cc, a

	mov a, ii
	subb a, #4
	mov dd, a

;(aa > dd)
	mov a, aa
	mov b, dd
	call compare
	jnc M1 ; if(aa>dd) goto M1
	
;(aa > dd) & (bb != cc) 
	mov a, cc
	cjne a, bb, M3 ; if(cc!=dd) goto M3

;(dd >= cc)
M1: 
	mov a, dd
	mov b, cc
	call compare
	jz M2
	jnc M3 ; if (bb==0) goto M3
M2: 
	mov R7,#0x01
	sjmp M4
M3: 
	mov R7,#0x00 ; S=0
M4: 
	mov P3,R7 ; S=1
	
	inc ii
	sjmp loop
