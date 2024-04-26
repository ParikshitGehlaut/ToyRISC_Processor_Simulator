    .data
a:
    5
    6
    30
    24
    7
    10
n:
    6
number:
    24
    .text
main:
    add %x0, %x0, %x3
    load %x0, $n, %x10
    load %x0, $number, %x11
loop:
    load %x3, $a, %x4
    beq %x4, %x11, Success
    addi %x3, 1, %x3
    bgt %x3, %x10, Endnow
    jmp loop
Success:
    addi %x0, 1, %x16
    end
Endnow:
    subi %x0, 1, %x16
    end