#!/bin/bash

cc="$1"
[ -z "$cc" ] && exit 1

paste -d,                                                  \
    <(sed -n '/,/p' "$cc"/seq.txt)                         \
    <(sed -n '/,/p' "$cc"/par-2.txt | cut -d, -f2)         \
    <(sed -n '/,/p' "$cc"/par-4.txt | cut -d, -f2)         \
    <(sed -n '/,/p' "$cc"/par-8.txt | cut -d, -f2)         \
    <(sed -n '/,/p' "$cc"/par-16.txt | cut -d, -f2)        \
