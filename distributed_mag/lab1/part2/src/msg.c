#include <stdio.h>

#include <unistd.h>

#include "labdefs.h"
#include "banking.h"
#include "util.h"

void child_set_started_msg(char *msg, IOHandle *handle, void *data) {
	assert(data != NULL);
	BalanceHistory *balances = (BalanceHistory*)data;
	BalanceState state = balances[handle->src_pid].s_history[get_lamport_time()];
	snprintf(msg, MAX_PAYLOAD_LEN, log_started_fmt, get_lamport_time(),
			 handle->src_pid, getpid(), handle->parent_pid, state.s_balance);
}

void child_set_received_all_started_msg(char *msg, IOHandle *handle) {
	snprintf(msg, MAX_PAYLOAD_LEN, log_received_all_started_fmt,
			 get_lamport_time(), handle->src_pid);
}

void child_set_done_msg(char *msg, IOHandle *handle, void *data) {
	assert(data != NULL);
	BalanceHistory *balances = (BalanceHistory*)data;
	BalanceState state = balances[handle->src_pid].s_history[get_lamport_time()];
	snprintf(msg, MAX_PAYLOAD_LEN, log_done_fmt,
			 get_lamport_time(), handle->src_pid, state.s_balance);
}

void child_set_received_all_done_msg(char *msg, IOHandle *handle) {
	snprintf(msg, MAX_PAYLOAD_LEN, log_received_all_done_fmt,
			 get_lamport_time(), handle->src_pid);
}

