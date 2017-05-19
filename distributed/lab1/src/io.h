#ifndef _IO_H_
#define _IO_H_

#include <assert.h>
#include <sys/types.h>

#include "ipc.h"

typedef struct {
	int readfd, writefd;
}__attribute__((packed)) Channel;

typedef struct {
	local_id src_pid;
	Channel *channel_table;
	size_t proc_num;
	pid_t parent_pid;
	uint8_t mutex_enabled;
	local_id last_msg_pid;
	size_t done_cnt;
} IOHandle;

#endif
