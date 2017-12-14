#!/bin/sh

rman target / << EOF
STARTUP MOUNT;
run {
	CONFIGURE CHANNEL DEVICE TYPE DISK FORMAT '/u01/bcu71/loudoven/backup/rman/full_%u_%s_%p';
	CONFIGURE RETENTION POLICY TO RECOVERY WINDOW OF 7 DAYS;
	BACKUP AS BACKUPSET DATABASE PLUS ARCHIVELOG;
}
exit
EOF