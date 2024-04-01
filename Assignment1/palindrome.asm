	.data
a:
	10
	.text
main:
	load %x0, $a, %x11
	load %x0, $a, %x13
	addi %x0, 0, %x12
loop1:
	bgt %x11, %x0, loop
	beq %x13, %x12, pal
	jmp notpal
loop:
	divi %x11, 10, %x11
	muli %x12, 10, %x12
	add %x12, %x31, %x12
	jmp loop1 
pal:
	addi %x0, 1, %x10
	end
notpal:
	subi %x0, 1, %x10
	end
