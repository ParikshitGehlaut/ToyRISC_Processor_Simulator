
    .data
a:
    123
    234
    .text
main:
    load %x0, $a, %x4
    addi %x0, 1, %x3
    load %x3, $a, %x5
    add %x5, %x4, %x6
    end
