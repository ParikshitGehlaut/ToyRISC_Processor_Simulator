    .data
.a
    1729
    1
    .text
main:
    load %x0, $a, %x4
    addi %x0, 1, %x3
    load %x3, $a, %x5
    sub %x5, %x4, %x6
    end