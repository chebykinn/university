#!/bin/bash

export ORACLE_SID="s191954"
export ORACLE_HOME="/u01/app/oracle/product/11.2.0/dbhome_1"
export PATH=${PATH}:${ORACLE_HOME}/bin
export NLS_LANG="AMERICAN_AMERICA.UTF8"

export MP="/u01/bcu71/loudoven"
export RECDEST="$MP/flash_recovery_area"

if [ ! -e ${MP} ]; then
    mkdir -p ${MP}
    mkdir ${MP}/node01
    mkdir ${MP}/node02
    mkdir ${MP}/node03
    mkdir ${MP}/node04
fi

orapwd file="orapwdorcl"

if [ ! -e "$RECDEST" ]; then
    mkdir "$RECDEST"
fi

cp "init$ORACLE_SID.ora" "$ORACLE_HOME/dbs/"
sqlplus /nolog @create_db.sql
