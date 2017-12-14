#!/usr/bin/bash

cat << EOF | sqlplus / as sysasm
DROP DISKGROUP CUTEGOAT;
CREATE DISKGROUP CUTEGOAT NORMAL REDUNDANCY
	FAILGROUP f1 DISK '/u01/cutegoat/cutegoat0',
			  '/u01/cutegoat/cutegoat1'
	FAILGROUP f2 DISK '/u01/cutegoat/cutegoat3',
			  '/u01/cutegoat/cutegoat4';

EOF
