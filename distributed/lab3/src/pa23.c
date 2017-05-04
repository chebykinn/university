#include <getopt.h>
#include <unistd.h>
#include <stdlib.h>
#include <stdio.h>
#include <assert.h>
#include <string.h>

#include "banking.h"
#include "labdefs.h"
#include "util.h"

timestamp_t proc_time = 0;

timestamp_t get_lamport_time(){
	return proc_time;
}

int parent_work(IOHandle *handle) {
    bank_robbery(handle, handle->proc_num - 1);
    proc_time++;
	Message stop;
	stop.s_header.s_magic = MESSAGE_MAGIC;
	stop.s_header.s_type = STOP;
	stop.s_header.s_local_time = get_lamport_time();
    int rc = send_multicast(handle, &stop);
    if( rc != 0 ) return rc;

	return 0;
}

int parent_atexit(IOHandle *handle){
	AllHistory all;

	all.s_history_len = handle->proc_num - 1;

	Message msg;
	for(local_id i = 1; i < handle->proc_num; i++){
		int rc = receive(handle, i, &msg);
		if( rc != 0 ) return 1;
		memcpy(&all.s_history[i - 1], msg.s_payload, msg.s_header.s_payload_len);
	}

	print_history(&all);
	return 0;
}

void update_balance(BalanceHistory *history, TransferOrder *order){
	timestamp_t current_time = get_lamport_time();
	balance_t prev;
	prev = history->s_history_len == 0
			? 0 : history->s_history[history->s_history_len - 1].s_balance;

	timestamp_t new_len = current_time + 1;

	if( new_len > 1 ) {
		for(timestamp_t t = history->s_history_len; t < new_len; t++){
			history->s_history[t] = history->s_history[t - 1];
			history->s_history[t].s_time++;
		}
	}

	history->s_history[current_time].s_time = current_time;
	history->s_history[current_time].s_balance_pending_in =
		order->s_src != history->s_id ? order->s_amount : 0;
	history->s_history[current_time].s_balance =
		order->s_src == history->s_id ? prev - order->s_amount
									  : prev + order->s_amount;

	history->s_history_len = current_time + 1;
}

int child_work(IOHandle *handle, void *data) {
	assert(data != NULL);
	BalanceHistory *balances = (BalanceHistory*)data;
	BalanceHistory *history = &balances[handle->src_pid];
	char buff[MAX_PAYLOAD_LEN];
	// Receive transfer message
	Message msg;
	TransferOrder order;
	memset(&msg, 0, sizeof msg);
	memset(&order, 0, sizeof order);
	while( 1 ){
		proc_time++;
		int rc = receive_any(handle, &msg);
		if( rc != 0 ) return 1;

		switch(msg.s_header.s_type){
			case TRANSFER:
				memcpy(&order, msg.s_payload, msg.s_header.s_payload_len);
				proc_time++;
				if( order.s_src == handle->src_pid ) {
					snprintf(buff, MAX_PAYLOAD_LEN, log_transfer_out_fmt,
							 get_lamport_time(), handle->src_pid,
							 order.s_amount, order.s_dst);
					log_event(buff);

					update_balance(history, &order);
					// Send transfer message to dest
					rc = send(handle, order.s_dst, &msg);
					if( rc != 0 ) return 1;
				}else{
					// Send ACK message
					msg.s_header.s_type = ACK;
					msg.s_header.s_local_time = get_lamport_time();
					msg.s_header.s_payload_len = 0;
					snprintf(buff, MAX_PAYLOAD_LEN, log_transfer_in_fmt,
							 get_lamport_time(), handle->src_pid,
							 order.s_amount, order.s_src);
					log_event(buff);

					rc = send(handle, 0, &msg);
					if( rc != 0 ) return 1;

					update_balance(history, &order);
				}
			break;

			case STOP:
				order.s_src = 0;
				order.s_dst = handle->src_pid;
				order.s_amount = 0;
				update_balance(history, &order);
				return 0;
			break;
		}

	}

	return 0;
}

int child_atexit(IOHandle *handle, void *data){
	BalanceHistory *balances = (BalanceHistory*)data;
	BalanceHistory *history = &balances[handle->src_pid];

	Message msg;
	msg.s_header.s_magic = MESSAGE_MAGIC;
	msg.s_header.s_type = BALANCE_HISTORY;
	msg.s_header.s_local_time = get_lamport_time();
	msg.s_header.s_payload_len =
		sizeof *history - (MAX_T + 1 - history->s_history_len) * sizeof *history->s_history;

	memcpy(msg.s_payload, history, msg.s_header.s_payload_len);
	int rc = send(handle, 0, &msg);
	return rc;
}

void transfer(void * parent_data, local_id src, local_id dst,
              balance_t amount) {
    Message msg;
    memset(&msg, 0, sizeof msg);
    msg.s_header.s_magic = MESSAGE_MAGIC;
    msg.s_header.s_type = TRANSFER;
    msg.s_header.s_local_time = get_lamport_time();

    TransferOrder order;
    order.s_src = src;
    order.s_dst = dst;
    order.s_amount = amount;

    msg.s_header.s_payload_len = sizeof order;
    memcpy(msg.s_payload, &order, msg.s_header.s_payload_len);

    int rc = send(parent_data, src, &msg);
    if( rc != 0 ) exit(1);

    rc = receive(parent_data, dst, &msg);
    if( rc != 0 ) exit(1);
}

int main(int argc, char * argv[]) {
	int rc = 0;

	int proc_num = get_proc_num_from_args(argc, argv);
	if( proc_num < 0 ) return 1;

	if( argc - optind < proc_num - 1 ) return 1;

	BalanceHistory *balances = calloc(1, proc_num * sizeof *balances);
	if( balances == NULL ) return 2;

	TransferOrder order;
	memset(&order, 0, sizeof order);
	for(int i = 1; i < proc_num; i++){
		balances[i].s_id = i;
		order.s_dst = i;
		order.s_amount = atoi(argv[optind + i - 1]);
		balances[i].s_history_len = 0;
		update_balance(&balances[i], &order);
	}

	IOHandle handle;
	rc = create_handle(proc_num, &handle);
	if( rc != 0 ) return rc;

	rc = create_pipes(&handle);
	if( rc != 0 ) return rc;

	int is_parent = spawn_childs(&handle);
	if( is_parent < 0 ) return 2;

	if( is_parent ){
		rc = parent(&handle);
	}else{
		rc = child(&handle, balances);
	}

	free(handle.channel_table);
	free(balances);
	return rc;

}
