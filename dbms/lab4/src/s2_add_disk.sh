#!/usr/bin/bash

/usr/sbin/mkfile -n 666m /u01/cutegoat/cutegoat7

cat << 'EOF' | sqlplus / as sysasm
ALTER DISKGROUP CUTEGOAT ADD DISK '/u01/cutegoat/cutegoat7';
exit;
EOF

