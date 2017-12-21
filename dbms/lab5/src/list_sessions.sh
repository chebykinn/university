#!/bin/sh
[ -z "$1" ] && exit 1
INST="$1"

SQL="SELECT sid , serial#, inst_id FROM gv\$session WHERE inst_id =$INST;"

echo "$SQL"$'\n' | sqlplusdb
