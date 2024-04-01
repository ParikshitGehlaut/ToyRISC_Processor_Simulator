	.data
a:
	10
	.text
main:
	load %x0, $a, %x12
	divi %x12, 2, %x13
	beq %x31, %x0, even
	addi %x0, 1, %x10
	end
even:
	subi %x0, 1, %x10
	end
