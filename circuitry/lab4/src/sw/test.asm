.global entry

.data                  /* data section*/
        .word 0x0001
        .word 0x0001
        .word 0xFFFF
.text                  /* code goes to text section*/
.ent entry
entry:
	sw $t0, 0x400

	jal .check
	addi $t0, $t0, 1
	j entry

	.check:
		addi $t0, $t0, 1
		jr $t31
		j entry
.end entry
