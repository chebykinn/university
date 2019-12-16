#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <unistd.h>

#include "io.h"
#include "ipc.h"
#include "pa1.h"
#include "util.h"

int parent_work(IOHandle *handle) { return 0; }

int child_work(IOHandle *handle, void *data) { return 0; }

void child_set_started_msg(char *msg, IOHandle *handle, void *data) {
    snprintf(msg, MAX_PAYLOAD_LEN, log_started_fmt, handle->src_pid, getpid(),
             handle->parent_pid);
}

void child_set_received_all_started_msg(char *msg, IOHandle *handle) {
    snprintf(msg, MAX_PAYLOAD_LEN, log_received_all_started_fmt,
             handle->src_pid);
}

void child_set_done_msg(char *msg, IOHandle *handle, void *data) {
    snprintf(msg, MAX_PAYLOAD_LEN, log_done_fmt, handle->src_pid);
}

void child_set_received_all_done_msg(char *msg, IOHandle *handle) {
    snprintf(msg, MAX_PAYLOAD_LEN, log_received_all_done_fmt, handle->src_pid);
}

int main(int argc, char *const argv[]) {
    int rc = 0;

    int proc_num = get_proc_num_from_args(argc, argv, NULL);
    if (proc_num < 0) return 1;

    IOHandle handle;
    rc = create_handle(proc_num, &handle);
    if (rc != 0) return rc;

    rc = create_pipes(&handle);
    if (rc != 0) return rc;

    int is_parent = spawn_childs(&handle);
    if (is_parent < 0) return 2;

    if (is_parent) {
        rc = parent(&handle);
    } else {
        rc = child(&handle, NULL);
    }

    free(handle.channel_table);
    return rc;
}
