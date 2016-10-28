Dseg at 8
S: ds 2
Cseg at 0
Jmp start

start: 
	clr a
	mov r0,#8
	mov r1,P1		//in r1 - dividend
	mov r2,P2		//in r2 - divider

loop:
	clr c
	mov b,r1
	mov a,r2
	rr a			//r2 div on 2 for comparing
	anl a,#0x7f
	xch a,b
	subb a,b
	jc below_zero

above_zero:		// r1*2 > r2
	clr a
	mov a,r1
	mov b,r2
	subb a,b
	mov b,r1
	add a,b
	mov r1,a
	clr a
	mov a,r3
	rl a
	inc a
	mov r3,a
	jmp finish

below_zero:		//r1*2 < r2
	mov a,r1
	rl a
	anl a,#0xfe
	mov r1,a
	mov a,r3
	rl a
	mov r3,a

finish:
	djnz r0,loop
	mov P3,r3
	
	jmp $
end
