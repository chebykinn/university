#!/bin/sh

rman target / << EOF
STARTUP MOUNT;
run {
	BACKUP DATABASE PLUS ARCHIVELOG;
}
exit
EOF
