jal main


prime_and_overwrite:
	addi t1, zero, 0		# Initialize a counter for prime numbers
	addi t5, zero, 2
	lw t4, 4(x0) 		#count for array length
	
	loop:
		addi t2, zero, 2		# Initial divisor for prime check (right now 2)
		addi t3, zero, 0
		blt t4, t1, return		# exit loop if length < increase is same
		
		lw a1, 0(a0)		# Load the current array element
		blt a1, t2, not_prime	# Check if the current element is less than
					# if so its not prime
		loop2:
			div t3, a1, t5
			blt t3, t2, break_loop2
			rem a2, a1, t2		#check for divisibility
			beq a2, zero, not_prime	#if % = 0 then not prime
			
			addi t2, t2, 1
			jal loop2
			
		break_loop2: 
			jal prime
		
		
	next:
		
		addi a0, a0, 4		#move to next element
		addi t1, t1, 1		#increment prime counter
		jal loop		#return to main
		
	not_prime:
			sw zero, 0(a0)
			jal next
		
		
	prime:
			addi t0, zero, 1
			sw t0, 0(a0)
			jal next
	
	return:
		jal come_back
		

main:
	#lw a0, array
	#auipc
	lw s0, 8(x0)
	lw a0, 0(s0)
	jal prime_and_overwrite
	come_back:
	
