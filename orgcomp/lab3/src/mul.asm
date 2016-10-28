Dseg at 8
S: ds 2
Cseg at 0
Jmp start

start: 
	clr a
	mov r0,#8
	mov r1,p1     ;p2(B) - in Acc

loop:
	mov a,r1
	rrc a
	jnc no_add
	
	;add SHigh <- a
	mov a,r2
	add a,p0      ;p1(A) - in Acc
	mov r2,a
	mov a,r3
	addc a,#0
	mov r3,a

no_add:
	;shift r3 -> r2
	mov a,r3
	rrc a
	clr a
	mov r3,a
	
	;shift r2
	addc a,#0
	rr a
	mov b,a
	mov a,r2
	rr a
	mov r4,a
	anl a,#7Fh
	add a,b
	mov r2,a

	;shift r1
	mov a,r4
	anl a,#80h
	mov b,a
	mov a,r1
	rr a
	anl a,#7Fh
	add a,b
	mov r1,a
	djnz r0,loop
	
	mov p2,r2
	mov p3,r1
	
	; increment pointer in dptr
	; put output in r1 and r2
	mov DPL,#S
	mov a,r2
	movx @dptr,a
	inc dptr
	mov a,r1
	movx @dptr,a 
	
	jmp $
end
