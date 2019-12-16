#!/bin/bash

labnum="$1"
[ -z "$labnum" ] && exit 1

lab="lab$labnum"
out="pa$labnum"
CLANG_ARGS=""

[ -z "$lab" ] && exit 1
[ -z "$out" ] && exit 1

rm -rf "$out"
mkdir "$out" || exit 2
find "$lab/src" "$lab/include" -name '*.h' -exec cp {} "$out"/ \;
find "$lab/src" "$lab/include" -name '*.c' -exec cp {} "$out"/ \;

if [ "$labnum" -gt 1 ]; then
    cp "$lab/lib64/libruntime.so" "$out"/
    CLANG_ARGS="-L./$out -lruntime"
fi

clang -std=c99 -Wall -pedantic $CLANG_ARGS ./"$out"/*.c || exit 2
rm -f ./"$out/libruntime.so"
rm -f ./a.out

tar -cvzf "$out".tar.gz "$out"/
