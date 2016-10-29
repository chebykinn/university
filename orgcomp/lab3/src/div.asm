i equ r0
wrk equ r1
cseg at 0
jmp start
start:
	mov A, P0
	mov B, P1
	mov wrk, P2
	subb A, wrk
	jc sta
	setb ov
	jmp out
sta:
	mov i, #10
m1:
	add A, wrk
	clr c
cikl:
	djnz i,m2
	jmp out
m2:
	xch A, B
	rlc A
	xch A,B
	rlc A
	mov F0, C
	subb A, wrk
	jb F0, cikl
	jc m1
	setb c
	jmp cikl
out:
	mov P3, B
	sjmp $
	end

