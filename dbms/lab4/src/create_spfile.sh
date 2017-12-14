#!/usr/bin/bash

srvctl add asm -p '+CUTEGOAT' -d '+CUTEGOAT'

sqlplus / as sysasm @setup_spfile.sql
