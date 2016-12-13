.global entry

.data                  /* data section*/
        .word 0x0001
        .word 0x0001
        .word 0xFFFF
.text                  /* code goes to text section*/
.ent entry
entry:
	addi $t0, $t0, 0x1488
	.loop:
		jal .iter
		nop
		beq $t0, $t1, .eq
		j .loop
		.iter:
			addi $t1, $t1, 1
			jr $ra
		.eq:
			addi $t2, $t2, 1
			sw $t2, 0x400
			andi $t1, $t1, 0
			j .loop

.end entry
