#!/bin/bash

cc="$1"
[ -z "$cc" ] && exit 1

paste -d,                                               \
    <(sed -n '/,/p' "$cc"/n1.csv)                       \
    <(sed -n '/,/p' "$cc"/n2.csv | cut -d, -f2)         \
    <(sed -n '/,/p' "$cc"/n4.csv | cut -d, -f2)         \
    <(sed -n '/,/p' "$cc"/n8.csv | cut -d, -f2)         \
    <(sed -n '/,/p' "$cc"/n16.csv | cut -d, -f2)        \
