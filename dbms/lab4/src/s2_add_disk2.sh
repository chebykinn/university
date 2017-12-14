
#!/usr/bin/bash

/usr/sbin/mkfile -n 666m /u01/luckyowl/luckyowl6

cat << 'EOF' | sqlplus / as sysasm
ALTER DISKGROUP LUCKYOWL ADD DISK '/u01/luckyowl/luckyowl6';

EOF
