i equ r0
wrk equ r1
sbit Bi=B^0
Cseg at 0
jmp start

start: 
	mov wrk, P0
	clr a
	mov B, P1
	mov i, #8
cikl:
	jnb Bi,m1
	add a,wrk

m1:
	rrc a
	xch a,b
	rrc a
	clr c
	xch a,b
	djnz i,cikl
	mov P2, a
	mov P3, b
	nop
end

