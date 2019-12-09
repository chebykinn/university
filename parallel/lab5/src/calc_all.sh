#!/bin/bash
for sched in static dynamic guided; do
    for chunk in 1 2 4 8 16; do
        echo calc $sched $chunk
        ./calc.sh "${sched}_${chunk}" > ${sched}/n$chunk.csv
    done
done
