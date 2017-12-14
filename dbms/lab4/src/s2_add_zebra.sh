#!/usr/bin/bash

. ./asm_env

. ./common

create_disk interestingzebra 3 666M

add_diskgroup interestingzebra 3 2M
