
# Define an array of integers
.data
array: .word 5, 10, 15, 20, 25  # Initialize an array with values

.text
# Load the address of the array and the array size into registers s3 and s2
la s3, array  # Load the address of the array into s3
li s2, 5      # Load the size of the array (5 elements) into s2

#initialization of required variables
addi t2, x0, 0 	# declare int i = 0 for loop
lw a4, 0(s3)   	# Load the first element of the array


#start of for loop
loop:
	beq  t2, s2, end    	# if t2 == s2, go to label done and break the cycle 
  	jal prime_check
  	jal loop         			# jump to  **for** label


# Prime number checking subroutine
prime_check:
 	addi t5, x0, 2       			# insert initial 2 to check prime number
 	beq a4, t5, isPrimeNumber		# it means if its = 2 its prime
 	inside_loop:
  		beq t5, a4, isPrimeNumber  	# if t5 == a4
  		rem t6, a4, t5			# find % value and write in t6
  		beq t6, x0, is_notPrimeNumber	#basically if % = 0 then not prime
  		addi t5, t5, 1       		# increment for checking prime number  
 		jal inside_loop			# jump back to inside_loop
 	
 	come_back:			# jump to prime
  		sw a0, 0(s3)			# Store the result in the array
  		addi s3, s3, 4    		# increment offset and move to the other value in the array
  		addi t2, t2, 1    		# increment number of passes through the cycle (i++)
  		lw a4, 0(s3)			# Load the next element of the array
		ret
	
	# non-prime subroutine that accepts 1 argument
	is_notPrimeNumber:
		addi a0, x0, 0	# if not prime replace with 0
  		jal come_back	#jal to come_back
  		
  	# prime subroutine that accepts 1 argument
	isPrimeNumber:
  		addi a0, x0, 1	#if prime replace with 1
  		jal come_back	# jump to come_back

  
# last ending state
end:  
