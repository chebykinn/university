#!/bin/sh
[ -z "$1" ] && exit 1
USER="$1"

SQL="SELECT sid , serial#, inst_id FROM gv\$session WHERE username=upper('$USER');"

echo "$SQL"$'\n' | sqlplusdb
