#define _GNU_SOURCE
#include "util.h"

#include <assert.h>
#include <fcntl.h>
#include <getopt.h>
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <sys/wait.h>
#include <unistd.h>

#include "common.h"
#include "io.h"
#include "labdefs.h"

IOHandle g_handle;

static int events_log_fd = -1;

static const char e_no_args[] = "Error: not enough arguments\n";
static const char e_proc_num[] =
    "Error: number of processes should be from 1 to 10\n";

static const char e_log_write[] = "Error: failed to write event read log\n";
static const char e_log_multicast[] = "Error: failed to send multicast\n";
static const char e_log_multicast_read[] = "Error: failed to read multicast\n";

void create_message(Message *msg, MessageType type, size_t length) {
    memset(msg, 0, sizeof *msg);
    msg->s_header.s_magic = MESSAGE_MAGIC;
    msg->s_header.s_type = type;
    msg->s_header.s_local_time = 0;
    msg->s_header.s_payload_len = length;
    const timevector_t local_time = get_vector_timevec();
    for (int i = 0; i < MAX_PROCESS_ID; i++)
        msg->s_header.s_local_timevector[i] = local_time.vec[i];
}

void create_vtime_message(Message *msg, timestamp_t vec_time) {
    create_message(msg, SNAPSHOT_VTIME, sizeof(vec_time));
    memmove(msg->s_payload, &vec_time, sizeof(vec_time));
}

void create_balance_message(Message *msg, balance_t balance, timestamp_t* vec_time) {
    BalanceState state = {0};

    create_message(msg, BALANCE_STATE, sizeof(BalanceState));
    state.s_balance = balance;
    for (int i = 0; i < MAX_PROCESS_ID; i++)
        state.s_timevector[i] = vec_time[i];

    memmove(msg->s_payload, &state, sizeof(state));
}

int log_event(char *msg) {
    assert(events_log_fd > 0 && "Called before opening log file");
    int rc = write(events_log_fd, msg, strlen(msg));
    if (rc < 0) {
        (void)write(STDERR_FILENO, e_log_write, sizeof e_log_write);
        return 1;
    }
    return 0;
}

static void close_pipes(IOHandle *handle) {
    for (int i = 0; i < handle->proc_num; i++) {
        for (int j = 0; j < handle->proc_num; j++) {
            if (i == j) continue;
            if (i != handle->src_pid) {
                close(handle->channel_table[i * handle->proc_num + j].writefd);
            }
            if (j != handle->src_pid) {
                close(handle->channel_table[i * handle->proc_num + j].readfd);
            }
        }
    }
}

static int receive_all(IOHandle *handle) {
    assert(handle != NULL);
    Message msg;
    create_message(&msg, 0, 0);
    for (local_id i = 1; i < handle->proc_num; i++) {
        int rc = receive(handle, i, &msg);
        if (rc != 0) {
            (void)write(STDERR_FILENO, e_log_multicast_read,
                        sizeof e_log_multicast);
            return 1;
        }
        set_max_time(msg.s_header.s_local_timevector);
        move_time();
    }
    return 0;
}

int get_proc_num_from_args(int argc, char *const argv[], void *data) {
    if (argc < 2) {
        (void)write(STDERR_FILENO, e_no_args, sizeof e_no_args);
        return -1;
    }

    int proc_num = 0;
    char *endp = NULL;
    switch (getopt(argc, argv, "p:")) {
        case 'p':
            proc_num = strtoul(optarg, &endp, 10);
            if (*endp != '\0' || proc_num == 0 || proc_num > 10) {
                (void)write(STDERR_FILENO, e_proc_num, sizeof e_proc_num);
                return -1;
            }
            break;
        case -1:
            return -1;
    }
    proc_num++;  // include parent id

    return proc_num;
}

int create_handle(int proc_num, IOHandle *handle) {
    Channel *channel_table =
        calloc(1, proc_num * proc_num * sizeof *channel_table);
    if (channel_table == NULL) return 1;

    memset(handle, 0, sizeof *handle);
    handle->proc_num = proc_num;
    handle->channel_table = channel_table;
    handle->parent_pid = getpid();
    return 0;
}

int create_pipes(IOHandle *handle) {
    int pipes_log_fd =
        open(pipes_log, O_CREAT | O_WRONLY | O_TRUNC | O_APPEND, 0644);
    if (pipes_log_fd < 0) return 1;

    char msg[64];
    for (int32_t i = 0; i < handle->proc_num; i++) {
        for (int32_t j = 0; j < handle->proc_num; j++) {
            if (i == j) continue;
            int rc =
                pipe2((int *)&handle->channel_table[i * handle->proc_num + j],
                      O_NONBLOCK);
            snprintf(msg, 64, "opened pipe(%d, %d)\n", i, j);
            int n = write(pipes_log_fd, msg, strlen(msg));
            if (n < 0) return 1;
            if (rc < 0) return 1;
        }
    }
    return 0;
}

int spawn_childs(IOHandle *handle) {
    events_log_fd =
        open(events_log, O_CREAT | O_WRONLY | O_TRUNC | O_APPEND, 0644);
    if (events_log_fd < 0) return 1;

    int is_parent = 1;
    for (local_id pid = 1; pid < handle->proc_num; pid++) {
        int sys_pid = fork();
        if (sys_pid < 0) return -1;
        if (sys_pid == 0) {
            is_parent = 0;
            handle->src_pid = pid;
            break;
        }
    }

    return is_parent;
}

int parent(IOHandle *handle) {
    close_pipes(handle);
    int rc = 0;
    rc = receive_all(handle);
    if (rc != 0) return 1;

    // Do work
    rc = parent_work(handle);
    if (rc != 0) return rc;

    rc = receive_all(handle);
    if (rc != 0) return 1;

    rc = parent_atexit(handle);
    if (rc != 0) return 1;

    for (local_id pid = 1; pid < handle->proc_num; pid++) {
        wait(NULL);
    }
    return 0;
}

int child(IOHandle *handle, void *data) {
    close_pipes(handle);
    char log_buff[MAX_PAYLOAD_LEN];
    int rc = 0;
    move_time();

    Message msg;
    create_message(&msg, STARTED, 0);
    child_set_started_msg(msg.s_payload, handle, data);
    msg.s_header.s_payload_len = strlen(msg.s_payload);

    rc = log_event(msg.s_payload);
    if (rc != 0) return 1;

    rc = send_multicast(handle, &msg);
    if (rc != 0) {
        (void)write(STDERR_FILENO, e_log_multicast, sizeof e_log_multicast);
        return 1;
    }

    rc = receive_all(handle);
    if (rc != 0) return 1;

    child_set_received_all_started_msg(log_buff, handle);

    rc = log_event(log_buff);
    if (rc != 0) return 1;

    // Do work
    rc = child_work(handle, data);
    if (rc != 0) return rc;

    child_set_done_msg(msg.s_payload, handle, data);
    move_time();
    msg.s_header.s_local_time = get_vector_time();

    msg.s_header.s_payload_len = strlen(msg.s_payload);
    msg.s_header.s_type = DONE;

    rc = log_event(msg.s_payload);
    if (rc != 0) return 1;

    rc = send_multicast(handle, &msg);
    if (rc < 0) {
        (void)write(STDERR_FILENO, e_log_multicast, sizeof e_log_multicast);
        return 1;
    }

    rc = receive_all(handle);
    if (rc != 0) return 1;

    child_set_received_all_done_msg(log_buff, handle);
    rc = log_event(log_buff);
    if (rc != 0) return 1;

    return child_atexit(handle, data);
}

void total_sum_snapshot() {
    IOHandle *handle = &g_handle;
    int8_t acked = 0;
    int8_t balance_answered = 0;
    Message tx_msg;
    timestamp_t snap_time = get_vector_time() + 1;
    balance_t total = 0;
    timevector_t v;
    int rc = 0;

    create_vtime_message(&tx_msg, snap_time);
    rc = send_multicast(handle, &tx_msg);
    if (rc != 0) {
        fprintf(stderr, "send multicast vtime failed\n");
        return;
    }

    while (acked < handle->proc_num - 1) {
        Message msg;
        rc = receive_any(handle, &msg);
        if (rc != 0) {
            fprintf(stderr, "receive any total_sum failed\n");
            return;
        }

        if (SNAPSHOT_ACK == msg.s_header.s_type) {
            acked++;
        } else {
            fprintf(stderr, "total_sum: invalid message %d\n", msg.s_header.s_type);
            return;
        }
    }

    move_time();
    create_message(&tx_msg, EMPTY, 0);
    if (send_multicast(handle, &tx_msg) != 0) {
        fprintf(stderr, "send multicast empty failed\n");
        return;
    }

    while (balance_answered < handle->proc_num - 1) {
        Message msg;
        rc = receive_any(handle, &msg);
        if (rc != 0) {
            fprintf(stderr, "receive any balance total_sum failed\n");
            return;
        }

        set_max_time(msg.s_header.s_local_timevector);
        move_time();

        if (BALANCE_STATE == msg.s_header.s_type) {
            balance_answered++;
            total += ((BalanceState *)msg.s_payload)->s_balance;
        } else {
            fprintf(stderr, "total_sum: invalid balance message\n");
            return;
        }
    }

    v = get_vector_timevec();
    printf(format_vector_snapshot, v.vec[0], v.vec[1], v.vec[2], v.vec[3],
           v.vec[4], v.vec[5], v.vec[6], v.vec[7], v.vec[8], v.vec[9],
           v.vec[10], total);
}
