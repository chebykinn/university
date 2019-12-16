#include <stdio.h>
#include <stdlib.h>
#include <unistd.h>

#include "queue.h"
#include "util.h"
#include "forks.h"

ForkData *fork_data = NULL;

void fork_init(IOHandle *handle, ForkData **fork) {
    *fork = calloc(1, sizeof(ForkData));
    ForkData *fork2 = *fork;

    for (int i = 1; i <= handle->proc_num - 1; i++) {
        fork2->clean[i] = fork2->dirty[i] = fork2->reqf[i] = 0;
        if (i > handle->src_pid) {
            fork2->clean[i] = fork2->dirty[i] = 1;
        }
        if (i < handle->src_pid) {
            fork2->reqf[i] = 1;
        }
    }
}

int fork_check_forks(IOHandle *handle, ForkData *fork) {
    for (int i = 1; i <= handle->proc_num - 1; i++) {
        if (i == handle->src_pid) {
            continue;
        }
        if (!fork->clean[i]) {
            return 1;
        }
    }
    return 0;
}

int fork_set_all_dirty(IOHandle *handle, ForkData *fork) {
    for (int i = 1; i <= handle->proc_num - 1; i++) {
        if (i == handle->src_pid) {
            continue;
        }
        fork->dirty[i] = 1;
    }
    return 0;
}
