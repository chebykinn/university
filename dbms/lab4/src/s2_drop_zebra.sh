#!/usr/bin/bash

. ./asm_env

. ./common

cat << EOF | sqlplus / as sysasm
drop diskgroup interestingzebra including contents;

EOF
