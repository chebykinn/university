#include <stdio.h>
#include "util.h"
#include "labdefs.h"

int rq_time = -1;

int delayed[10];

int request_cs(const void * self) {
	IOHandle *handle = (IOHandle*)self;
	Message msg;
	move_time();
	create_message(&msg, CS_REQUEST, 0);

	int rc = send_multicast(handle, &msg);
	if( rc != 0 ) return rc;

	rq_time = get_lamport_time();

	int replies_cnt = handle->proc_num - 2;
	while( replies_cnt > 0  ){
		rc = receive_any(handle, &msg);
		if( rc != 0 ) return rc;
		set_max_time(msg.s_header.s_local_time);
		move_time();
		switch( msg.s_header.s_type ){
			case CS_REQUEST:
				if( rq_time == -1 || msg.s_header.s_local_time < rq_time
					|| ( msg.s_header.s_local_time == rq_time
					&& handle->last_msg_pid < handle->src_pid ) ){

					move_time();
					create_message(&msg, CS_REPLY, 0);
					int rc = send(handle, handle->last_msg_pid, &msg);
					if( rc != 0 ) return rc;
				}else{
					delayed[handle->last_msg_pid] = 1;
				}
			break;
			case CS_REPLY:
				replies_cnt--;
			break;
			case DONE:
				handle->done_cnt++;
			break;
		}
	}

	return 0;
}

int release_cs(const void * self) {
	IOHandle *handle = (IOHandle*)self;

	Message msg;
	move_time();
	create_message(&msg, CS_REPLY, 0);
	for(int i = 1; i <= handle->proc_num - 1; i++){
		if( !delayed[i] ) continue;
		move_time();
		msg.s_header.s_local_time = get_lamport_time();

		int rc = send(handle, i, &msg);
		if( rc != 0 ) return rc;
	}
	return 0;
}

