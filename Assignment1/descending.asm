	.data
a:
	70
	80
	40
	20
	10
	30
	50
	60
n:
	8
	.text
main:
	add %x0, %x0, %x3
	addi %x0, 1, %x9
	load %x0, $n, %x11
	add %x11, %x0, %x6
	add %x0, %x0, %x7
	jmp loop2
loop:
	addi %x0, 0, %x7
	addi %x0, 0, %x3
	addi %x9, 1, %x9
	subi %x6, 1, %x6
	beq %x9, %x11, endl
	jmp loop2
swap:
	store %x5, 0, %x7
	addi %x7, 1, %x7
	store %x4, 0, %x3
loop2:
	load %x3, $a, %x4
	addi %x3, 1, %x3
	beq %x3, %x6, loop
	load %x3, $a, %x5
	blt %x4, %x5, swap
	addi %x7, 1, %x7
	jmp loop2
endl:
	end
