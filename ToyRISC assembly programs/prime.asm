	.data
a:
	2
	.text
main:
	load %x0, $a, %x4
	addi %x0, 2, %x12
	beq %x0, %x12, prime
loop1:
	div %x4, %x12, %x5
	beq %x31, %x0, success
	bgt %x31, %x0, loop
loop:
	addi %x12, 1, %x12
	bgt %x4, %x12, loop1
	jmp prime
success:
	subi %x0, 1, %x10
	end
prime:
	addi %x0, 1, %x10
	end
