#ifndef _IO_H_
#define _IO_H_

#include <assert.h>


typedef struct {
	int readfd, writefd;
}__attribute__((packed)) Channel;

typedef struct {
	local_id src_pid;
	Channel *channel_table;
	size_t proc_num;
} IOHandle;

#endif
