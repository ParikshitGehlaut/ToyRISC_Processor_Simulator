	.data
n:
	10
	.text
main:
	load %x0, $n, %x4
	subi %x4, 2, %x4
	addi %x0, 1, %x5
	addi %x0, 65535, %x6
	addi %x0, 65534, %x7
	store %x0, 0, %x6
	store %x5, 0, %x7
loop3:
	beq %x4, %x0, loop1
	bgt %x4, %x0, loop2
loop2:
	subi %x4, 1, %x4
	load %x6, 0, %x10
	load %x7, 0, %x11
	add %x10, %x11, %x12
	subi %x7, 1, %x8
	store %x12, 0, %x8
	subi %x6, 1, %x6
	subi %x7, 1, %x7
	jmp loop3
loop1:
	end
