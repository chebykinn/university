#include <stdio.h>
#include <stdlib.h>

#include "labdefs.h"
#include "queue.h"
#include "util.h"
#include "forks.h"

int request_cs(const void *self) {
    IOHandle *handle = (IOHandle *) self;
    int rc = 0;
    Message msg;

    if (fork_data == NULL) {
        fork_init(handle, &fork_data);
    }

    while (fork_check_forks(handle, fork_data) != 0) {
        for (int i = 1; i <= handle->proc_num - 1; i++) {
            if (i == handle->src_pid) {
                continue;
            }
            if (fork_data->clean[i] || !fork_data->reqf[i]) {
                continue;
            }
            fork_data->reqf[i] = 0;
            move_time();
            create_message(&msg, CS_REQUEST, 0);
            rc = send(handle, i, &msg);
            if (rc != 0) {
                return rc;
            }
        }
        rc = child_handle_cs(handle);
        if (rc != 0) return rc;
    }
    return 0;
}

int release_cs(const void *self) {
    IOHandle *handle = (IOHandle *) self;
    int rc;
    Message msg;
    fork_set_all_dirty(handle, fork_data);

    for (int i = 1; i <= handle->proc_num - 1; ++i) {
        if (i == handle->src_pid) {
            continue;
        }
        if (!fork_data->reqf[i] || !fork_data->clean[i] || !fork_data->dirty[i]) {
            continue;
        }
        fork_data->clean[i] = fork_data->dirty[i] = 0;
        move_time();
        create_message(&msg, CS_REPLY, 0);
        rc = send(handle, i, &msg);
        if (rc != 0) {
            return rc;
        }
    }
    return 0;
}
