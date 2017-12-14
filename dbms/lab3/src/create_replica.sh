#!/bin/bash

. ./env

if [ ! -e "$REP_ORADATA" ]; then
	mkdir -p "$REP_ORADATA"
	mkdir "$REP_ORADATA/node01"
	mkdir "$REP_ORADATA/node02"
	mkdir "$REP_ORADATA/node03"
	mkdir "$REP_ORADATA/logs"
fi

sqlplus / as sysdba @setup_log.sql

for i in $(find "$ORADATA" -name "*.dbf"); do
	cp "$i" "$(echo $i | sed 's/bcu71/replica/')"
done

./do_replica.sh
