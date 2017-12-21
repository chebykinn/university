#!/bin/sh
[ -z "$1" ] && exit 1
NAME="$1"

sqlplusdb <<EOF
ALTER SESSION SET "_ORACLE_SCRIPT" = true;
CREATE USER $NAME IDENTIFIED BY "oracle";
GRANT CONNECT TO $NAME;

EOF
