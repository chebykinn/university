.global entry

.data                  /* data section*/
        .word 0x0001 
        .word 0x0001
        .word 0xFFFF   
.text                  /* code goes to text section*/
.ent entry
entry:
	    lw $t0, 0x200         /* t0 = 1*/
	    lw $t1, 0x201         /* t1 = 1*/
	    add $t0, $t0, $t1     /* t0 = t0 + t1 = 2*/ 
        sw $t0, 0x400         /* led = t0 */
        add $t0, $t0, 0xB     /* t0 = t0 + 0xB  == 0xD*/
        sw $t0, 0x400         /* led = t0 */
        sub $t0, $t0, $t1     /* t0 = t0 - $t1  == 0xC*/
        sw $t0, 0x400         /* led = t0 */  
        or $t0, $t0, 0x10     /* t0 = t0 | 0x10 == 0x1C*/
        sw $t0, 0x400         /* led = t0 */
        xor $t0, $t0, $t1     /* t0 = t0 ^ t1   == 0x1D*/
        sw $t0, 0x400         /* led = t0 */
        sw $t0, 0x800         /* ioctrl data_r = t0 */
        lw $t1, 0x800         /* t1 = ioctrl data_r */
        lw $t3, 0x400         /* t3 = sw */
        sub $t1, $t1, 0x1     /* t1 = t1 - 1 */
        sw $t1, 0x800         /* ioctrl data_r = t1 */
        slt $t0, $t0, $t1     /* t0 = t0 < t1   == 0*/
        add $t0, $t0, $t1     /* t0 = t0 + t1   == 1*/
        beq $t0, $t1, entry   /* PC = entry if t0 == t1*/
.end entry
