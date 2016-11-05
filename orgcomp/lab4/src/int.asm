Dseg at 0x08 ;размещение данных в сегменте Data
	;ai: ds 1
	s: ds 1
	x equ r0 ;требуется регистр в команде cjne
	ai equ r1 ;требуется регистр в команде cjne

	cseg at 0x0 ; начало программы в сегменте Code
	jmp start

Si1: ; Si в АСС
	; S= 0xff- (((y*S)>>8)*ai)>>8
	mov b, x
	mul ab ;y*S
	mov a, ai
	mul ab ;(( )>>8)*ai
	mov a, #0xff
	subb a, b ;возврат Si+1 в АСС

start: 
	mov x,#0
	;y=(x*x)>>8; S=0xff;

cikl: 
	;mov a,x
	;mov b,x
	;mul ab
	;mov y,b
	mov s,#0x01
	mov ai, #142 ;a1
	call Si1 ; S= 0xff- (((y*S)>>8)*6)>>8 )
	mov ai, #128 ;a2
	call Si1 ; S= 0xff- (((y*S)>>8)*13)>>8)
	mov ai, #64 ;a3
	call Si1 ; S= 0xff- (((y*S)>>8)*43)>>8)
	mov ai, #128 ;a3
	call Si1 ; S= 0xff- (((y*S)>>8)*43)>>8)
	;mov b,x ;S=(S*x)>>8
	;mul ab
	mov P3, b
	inc x
	cjne x, #0xff,cikl
	jmp start
	nop
	end
