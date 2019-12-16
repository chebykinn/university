#include <assert.h>
#include <getopt.h>
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <unistd.h>

#include "labdefs.h"
#include "util.h"

timestamp_t proc_time = 0;

timestamp_t get_lamport_time() { return proc_time; }

void set_max_time(timestamp_t new_time) {
    proc_time = new_time > proc_time ? new_time : proc_time;
}

void move_time() { proc_time++; }

int parent_work(IOHandle *handle) { return 0; }

int parent_atexit(IOHandle *handle) { return 0; }

#define BUFF_SIZE 4096
int child_work(IOHandle *handle, void *data) {

    char buff[BUFF_SIZE];

    int n = handle->src_pid * 5;
    for (int i = 1; i <= n; i++) {
        if (handle->mutex_enabled) request_cs(handle);
        snprintf(buff, sizeof(buff), log_loop_operation_fmt, handle->src_pid, i,
                 n);
        print(buff);
        if (handle->mutex_enabled) release_cs(handle);
    }
    return 0;
}

int child_atexit(IOHandle *handle, void *data) { return 0; }

int main(int argc, char *argv[]) {
    int rc = 0;

    int mutex_enabled = 0;
    int proc_num = get_proc_num_from_args(argc, argv, &mutex_enabled);
    if (proc_num < 0) return 1;

    IOHandle handle;
    rc = create_handle(proc_num, &handle);
    if (rc != 0) return rc;
    handle.mutex_enabled = mutex_enabled;

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
