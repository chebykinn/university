#!/usr/bin/bash

. ./asm_env

. ./common

create_disk luckyowl 6 666M

cat << EOF | sqlplus / as sysasm
drop diskgroup cutegoat including contents;

EOF

add_diskgroup luckyowl 6 16M
