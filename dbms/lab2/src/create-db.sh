#!/bin/sh
. ./env

export RECDEST="$MP/flash_recovery_area"

if [ ! -e "$MP" ]; then
    mkdir -p "$MP"
    mkdir "$MP/node01"
    mkdir "$MP/node02"
    mkdir "$MP/node03"
    mkdir "$MP/node04"
fi

orapwd file="orapwdorcl"

[ ! -e "$RECDEST" ] && mkdir "$RECDEST"

cp "init$ORACLE_SID.ora" "$ORACLE_HOME/dbs/"
sqlplus /nolog @create_db.sql
