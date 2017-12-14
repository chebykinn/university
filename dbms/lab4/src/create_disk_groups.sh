#!/usr/bin/bash
. ./asm_env

. ./common

/u01/app/11.2.0/grid/bin/crsctl start resource ora.cssd

create_disk cutegoat 7 666m
create_disk cleverdog 3 666m

cp "init$ORACLE_SID" "$ORACLE_HOME/dbs/init$ORACLE_SID"

sqlplus / as sysasm @setup_disk_groups.sql
