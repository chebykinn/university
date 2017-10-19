#!/bin/sh

rman target / << EOF
startup mount;
run {
	restore database;
	recover database noredo;
}
exit
EOF
