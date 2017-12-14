#!/bin/bash

. ./env

sqlplus / as sysdba <<EOF
alter system archive log current;
exit;
EOF

cp "$ORADATA/logs/"* "$REP_ORADATA/logs"

sqlplus / as sysdba @dump_log.sql
