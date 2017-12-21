#!/bin/sh
[ -z "$1" ] && exit 1
USER="$1"

echo "SELECT sid, serial#, inst_id FROM gv\$session WHERE
username = upper('"$USER"');" | \
sqlplusdb | grep -P  '^([\t ]*[0-9]+[\t ]*){3}$' \
| awk '{printf "ALTER SYSTEM KILL SESSION '"'"'%s,%s,@%s'"'"';\n", $1, $2, $3}' \
| sqlplusdb
