.data
.align 2

string:
	.asciz "Hello World!"
	
.text
j main


strlen:
	
	addi t1, zero, 0
	
	loop:
		lb t0, 0(a0)
		beq t0, zero, done
		addi t1, t1, 1
		addi a0, a0, 1
		beq zero, zero, loop

	done:
		addi a0, t1, 0
		ret
main:

	la a0, string
	jal strlen
