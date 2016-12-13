.global entry

.data                  /* data section*/
        .word 0x0001
        .word 0x0001
        .word 0xFFFF
.text                  /* code goes to text section*/
.ent entry
entry:
	andi $t0, 0
	addi $t0, $t0, 1
	lw $t1, 0x400
	beq $t0, $t1, .led

	lw $t0, 0x800
	sw $t0, 0x400
	j entry

	.led:
		andi $t1, 0
		lw $t0, 0x800
		beq $t0, $t1, .no_light
		.light:
			andi $t0, 0
			addi $t0, $t0, 0x0000
			j .set_led
		.no_light:
			andi $t0, 0
			addi $t0, $t0, 0xffff
		.set_led:
		sw $t0, 0x400
		j entry
		
.end entry
