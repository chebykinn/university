. ./env
rman target / \ << EOF
startup mount;
run { 
	backup database;
	backup archivelog all;
}
shutdown;
exit
EOF;
