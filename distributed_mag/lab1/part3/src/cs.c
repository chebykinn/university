#include <stdio.h>
#include "util.h"
#include "labdefs.h"
#include "queue.h"
Queue *queue = NULL;

int request_cs(const void * self) {
	IOHandle *handle = (IOHandle*)self;
	Message msg;
	move_time();
	create_message(&msg, CS_REQUEST, 0);
	if( queue == NULL ){
		queue_init(&queue);
	}

	queue_push(queue, handle->src_pid, get_lamport_time());
	int rc = send_multicast(handle, &msg);
	if( rc != 0 ) return rc;

	int replies_cnt = handle->proc_num - 2;
	while( replies_cnt > 0 || queue->front->pid != handle->src_pid ){
		rc = receive_any(handle, &msg);
		if( rc != 0 ) return rc;
		set_max_time(msg.s_header.s_local_time);
		move_time();
		switch(msg.s_header.s_type){
			case CS_REQUEST:
				queue_push(queue, handle->last_msg_pid, msg.s_header.s_local_time);
				move_time();
				create_message(&msg, CS_REPLY, 0);
				int rc = send(handle, handle->last_msg_pid, &msg);
				if( rc != 0 ) return rc;
			break;
			case CS_RELEASE:
				queue_pop(queue);
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

	queue_pop(queue);
	Message msg;
	move_time();
	create_message(&msg, CS_RELEASE, 0);

	int rc = send_multicast(handle, &msg);
	if( rc != 0 ) return rc;

	return 0;
}

