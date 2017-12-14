#!/bin/bash

. ./env

cp "$ORADATA/logs/"* "$REP_ORADATA/logs"

sqlplus / as sysdba @dump_log.sql
