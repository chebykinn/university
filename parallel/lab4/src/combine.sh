#!/bin/bash

cc="$1"
[ -z "$cc" ] && exit 1

paste -d,                                               \
    <(sed -n '/,/p' calc2.csv)                       \
