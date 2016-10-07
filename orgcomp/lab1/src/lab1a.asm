xseg
out_str: ds 30

cseg at 0
jmp _start
in_str: db "This programmator",0

_start:

mov R0, #0 // R0 is used for input str offset counter

mov dptr, #out_str // Saving 16-bit address of output str into DPTR
mov R2, dpl // And saving dptr in R2 and R3
mov R3, dph

__loop:
mov DPTR, #in_str
mov a, R0
movc a, @a+DPTR

jz __end // jump to end if \0 symbol

mov dpl, R2 // Restore saved address in DPTR
mov dph, R3

cjne a, #'a', __nocase // if not 'a', skipping

mov a, #'A'

__nocase:
movx @DPTR, a
inc DPTR // Inc DPTR
mov R2, dpl // save addr
mov R3, dph

inc R0 // Inc input string pointer

jmp __loop

__end:

end