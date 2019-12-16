#include <assert.h>
#include <getopt.h>
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <unistd.h>

#include "banking.h"
#include "labdefs.h"
#include "util.h"

static timevector_t vector_time = {.vec = {0}};
static unsigned int proc_id;
timestamp_t proc_time = 0;

void init_vector_time(unsigned int id) { proc_id = id; }

timestamp_t get_vector_time() { return vector_time.vec[proc_id]; }
timevector_t get_vector_timevec() { return vector_time; }

void set_max_time(const timestamp_t *time_from_msg) {
    for (int i = 0; i < MAX_PROCESS_ID; i++) {
        vector_time.vec[i] = time_from_msg[i] > vector_time.vec[i]
                                 ? time_from_msg[i]
                                 : vector_time.vec[i];
    }
}

void move_time() { vector_time.vec[proc_id]++; }

int parent_work(IOHandle *handle) {
    init_vector_time(0);
    bank_robbery(handle, handle->proc_num - 1);
    move_time();
    Message stop;
    create_message(&stop, STOP, 0);
    int rc = send_multicast(handle, &stop);
    if (rc != 0) return rc;

    return 0;
}

int parent_atexit(IOHandle *handle) {
    return 0;
}

void update_balance(BalanceHistory *history, TransferOrder *order,
                    timestamp_t send_time) {
    timestamp_t current_time = get_vector_time();
    balance_t prev;
    prev = history->s_history_len == 0
               ? 0
               : history->s_history[history->s_history_len - 1].s_balance;

    timestamp_t new_len = current_time + 1;

    if (new_len > 1) {
        for (timestamp_t t = history->s_history_len; t < new_len; t++) {
            history->s_history[t] = history->s_history[t - 1];
            history->s_history[t].s_time++;
        }
    }

    for (timestamp_t t = send_time; t < current_time; t++) {
        history->s_history[t].s_balance_pending_in = order->s_amount;
    }

    history->s_history[current_time].s_time = current_time;
    history->s_history[current_time].s_balance = order->s_src == history->s_id
                                                     ? prev - order->s_amount
                                                     : prev + order->s_amount;

    history->s_history_len = current_time + 1;
}

int child_work(IOHandle *handle, void *data) {
    assert(data != NULL);
    init_vector_time(handle->src_pid);
    BalanceHistory *balances = (BalanceHistory *)data;
    BalanceHistory *history = &balances[handle->src_pid];
    char buff[MAX_PAYLOAD_LEN];
    // Receive transfer message
    Message msg;
    timestamp_t snap_time = -1;
    TransferOrder order;
    create_message(&msg, 0, 0);
    memset(&order, 0, sizeof order);
    while (1) {
        int rc = receive_any(handle, &msg);
        if (rc != 0) return 1;
        if (msg.s_header.s_type != SNAPSHOT_VTIME) {
            set_max_time(msg.s_header.s_local_timevector);
            move_time();
        }
        switch (msg.s_header.s_type) {
            case TRANSFER:
                memcpy(&order, msg.s_payload, msg.s_header.s_payload_len);
                move_time();
                if (order.s_src == handle->src_pid) {
                    msg.s_header.s_local_time = get_vector_time();
                    snprintf(buff, MAX_PAYLOAD_LEN, log_transfer_out_fmt,
                             get_vector_time(), handle->src_pid, order.s_amount,
                             order.s_dst);
                    log_event(buff);

                    update_balance(history, &order, msg.s_header.s_local_time);
                    // Send transfer message to dest
                    rc = send(handle, order.s_dst, &msg);
                    if (rc != 0) return 1;
                } else {
                    update_balance(history, &order, msg.s_header.s_local_time);

                    // Send ACK message
                    create_message(&msg, ACK, 0);
                    snprintf(buff, MAX_PAYLOAD_LEN, log_transfer_in_fmt,
                             get_vector_time(), handle->src_pid, order.s_amount,
                             order.s_src);
                    log_event(buff);

                    rc = send(handle, 0, &msg);
                    if (rc != 0) return 1;
                }
                break;

            case STOP:
                order.s_src = 0;
                order.s_dst = handle->src_pid;
                order.s_amount = 0;
                update_balance(history, &order, get_vector_time());
                return 0;
                break;
            case SNAPSHOT_VTIME: {
                snap_time = *(timestamp_t *)msg.s_payload;
                Message snap_ack;
                create_message(&snap_ack, SNAPSHOT_ACK, 0);
                rc = send(handle, handle->last_msg_pid, &snap_ack);
                if (rc != 0) return 1;
                break;
            }
            case EMPTY: {
                const timevector_t t = get_vector_timevec();
                if (snap_time > -1 && t.vec[0] >= snap_time) {
                    BalanceState state =
                        balances[handle->src_pid]
                            .s_history[balances[handle->src_pid].s_history_len -
                                       1];
                    Message bal;
                    timevector_t vec;
                    move_time();
                    vec = get_vector_timevec();
                    create_balance_message(&bal, state.s_balance, vec.vec);
                    rc = send(handle, handle->last_msg_pid, &bal);
                    if (rc != 0) return 1;
                    snap_time = -1;
                }
                break;
            }
        }
    }

    return 0;
}

int child_atexit(IOHandle *handle, void *data) {
    return 0;
}

void transfer(void *parent_data, local_id src, local_id dst, balance_t amount) {
    move_time();
    Message msg;
    create_message(&msg, TRANSFER, 0);

    TransferOrder order;
    order.s_src = src;
    order.s_dst = dst;
    order.s_amount = amount;

    msg.s_header.s_payload_len = sizeof order;
    memcpy(msg.s_payload, &order, msg.s_header.s_payload_len);

    int rc = send(parent_data, src, &msg);
    if (rc != 0) exit(1);

    rc = receive(parent_data, dst, &msg);
    if (rc != 0) exit(1);
}

int main(int argc, char *argv[]) {
    int rc = 0;

    int proc_num = get_proc_num_from_args(argc, argv, NULL);
    if (proc_num < 0) return 1;

    if (argc - optind < proc_num - 1) return 1;

    BalanceHistory *balances = calloc(1, proc_num * sizeof *balances);
    if (balances == NULL) return 2;

    TransferOrder order;
    memset(&order, 0, sizeof order);
    for (int i = 1; i < proc_num; i++) {
        balances[i].s_id = i;
        order.s_dst = i;
        order.s_amount = atoi(argv[optind + i - 1]);
        balances[i].s_history_len = 0;
        update_balance(&balances[i], &order, 0);
    }

    // IOHandle handle;
    rc = create_handle(proc_num, &g_handle);
    if (rc != 0) return rc;

    rc = create_pipes(&g_handle);
    if (rc != 0) return rc;

    int is_parent = spawn_childs(&g_handle);
    if (is_parent < 0) return 2;

    if (is_parent) {
        rc = parent(&g_handle);
    } else {
        rc = child(&g_handle, balances);
    }

    free(g_handle.channel_table);
    free(balances);
    return rc;
}
