#!/usr/bin/bash

. ./asm_env

. ./common

create_disk popularshark 5 666M

add_diskgroup popularshark 5 8M
