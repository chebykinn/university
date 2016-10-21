Xseg at 20
buf: ds 2
Cseg at 0

mov DPTR, #40H; ;buffer

sjmp check_sign

input:
check_sign:
    mov a, P1
    anl a, #0xf0			 ; (xA>>4)
    swap a					 ; (xA>>4)
    subb a, #0x0f			 ; x-13

    jnz pos
    mov r3, #1				 ; neg flag

pos:
    mov a, P1				 ; lower half of P1
    anl a, #0x0f
    mov b, #100
    mul ab
    mov r6, a

    mov a, P0				 ; upper half of P1
    swap a
    anl a, #0x0f
    mov b, #10
    mul ab
    mov b, r6
    add a, b
    mov r6, a

    mov a, P0				 ; lower half of P1
    anl a, #0x0f
    mov b, r6
    add a, b
    mov r6, a

test_sign:
    mov a, r3
    subb a, #1
    jnz to_memory

    mov a, #0xff 		 	 ; two's complement
    subb a, r6
    inc a

to_memory:
    movx @dptr, a


output:
    mov r3, #0
    mov a, #0
    movx a, @dptr
    mov  r6, a
    jnb ACC.7, pos_out

neg_out:
    mov a, #0xFF			 ; inverse number back
    subb a, r6
    inc a
    mov r6, a
    mov r3, #1

pos_out:
    mov a, r6
    mov b, #10
    div ab
    mov r5, b

    mov b, #10
    div ab
    mov r4, a
    mov a, b
    swap a
    orl a, r5

    mov P2, a

    mov a, r4
    mov b, #10
    div ab

    clr a
    add a, r3
    jz done
    mov a, #0xf0

done:
    orl a, b
    mov P3, a

    jmp $
end
