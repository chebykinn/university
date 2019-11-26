#!/bin/bash

n1=10
n2=10
#delta=$(((n2 - n1) / 2))
delta=1
n="$n1"

n_line=""
ms_line=""
x_line=""

name="$1"
[ -z "$name" ] && exit 1

echo "N,$name(N)"
while : ; do
    out=$(build/lab3 "$n")
    ms=$(echo "$out" | grep passed: | awk '{print $6}')
    x=$(echo "$out" | grep X= | awk -F= '{print $3}')
    n_line="$n_line $n"
    ms_line="$ms_line $ms"
    x_line="$x_line $x"
    echo "$n,$ms"
    n=$((n + delta))
    [ "$n" -gt "$n2" ] && break
done

echo
echo "N $n_line"
echo "$name(N) $ms_line"
echo "X $x_line"
